package de.invesdwin.context.frege;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import frege.prelude.PreludeBase.TList;
import frege.runtime.Undefined;

// CHECKSTYLE:OFF
@NotThreadSafe
public class FregeScriptEngineTest {

    private ScriptEngine frege;

    @BeforeEach
    public void beforeTest() {
        final ScriptEngineManager factory = new ScriptEngineManager();
        this.frege = factory.getEngineByName("frege");
    }

    @Test
    public void testExpression() throws ScriptException {
        final Object actual = frege.eval("show $ take 10 [2,4..]");
        final Object expected = "[2, 4, 6, 8, 10, 12, 14, 16, 18, 20]";
        assertEquals(expected, actual);
    }

    @Test
    public void testDefinition() throws ScriptException {
        frege.eval("f x y = x + y");
        final Object actual = frege.eval("f 3 4");
        final Object expected = 7;
        assertEquals(expected, actual);
    }

    @Test
    public void testDefinitionWithTypeAnn() throws ScriptException {
        frege.eval("f :: Int -> Int -> Int\n" + "f x y = x + y");
        frege.eval("g :: Int -> Int -> Int\n" + "g x y = x + y");
        final Object actual = frege.eval("f 3 4");
        final Object expected = 7;
        assertEquals(expected, actual);
    }

    @Test
    public void testInlineDefinitionWithTypeAnn() throws ScriptException {
        frege.eval("type F = (forall b. [b] -> [b]) -> Int");
        frege.eval("g :: F -> Int; g f = f reverse");
        frege.eval("k2 (f :: [Int] -> [Int]) = 42");
        final Object expected = frege.eval("g k2");
        final Object actual = 42;
        assertEquals(expected, actual);
    }

    @Test
    public void testBinding() throws ScriptException {
        frege.put("bar :: Integer", new BigInteger("12312332142343244"));
        final Object actual = frege.eval("bar + 3.big");
        final Object expected = new BigInteger("12312332142343247");
        assertEquals(expected, actual);
    }

    @Test
    public void testBindingWithTypeAnn() throws ScriptException {
        frege.put("foo::String", "I am foo");
        final Object actual = frege.eval("\"Hello World, \" ++ foo");
        final Object expected = "Hello World, I am foo";
        assertEquals(expected, actual);
    }

    @Test
    public void testCompilable() throws ScriptException {
        final Compilable compilableFrege = (Compilable) frege;
        final CompiledScript compiled = compilableFrege.compile("fib = 0 : 1 : zipWith (+) fib (tail fib)");
        compiled.eval();
        final Object actual = frege.eval("show $ take 6 fib");
        final Object expected = "[0, 1, 1, 2, 3, 5]";
        assertEquals(expected, actual);
    }

    @Test
    public void testModule() throws ScriptException {
        frege.eval("module foo.Foo where { bar = \"I am bar from foo\"}");
        frege.eval("import foo.Foo");
        frege.eval("baz = bar");
        final Object actual = frege.eval("baz");
        final Object expected = "I am bar from foo";
        assertEquals(expected, actual);
    }

    @Test
    public void testModuleWithComment() throws ScriptException {
        frege.eval(
                "module foo.Foo where\n" + "\n" + "{--asdfasdf\n" + "sadfasdfsadf-}\n" + "\n" + "baz = \"I am foo!\"");
        frege.eval("import foo.Foo");
        final Object actual = frege.eval("baz");
        final Object expected = "I am foo!";
        assertEquals(expected, actual);
    }

    @Test
    public void testUnpackagedModule() throws ScriptException {
        frege.eval("module Bar where { bar = \"I am bar\"}");
        frege.eval("pure native bar Bar.bar :: String");
        final Object actual = frege.eval("bar");
        final Object expected = "I am bar";
        assertEquals(expected, actual);
    }

    @Test
    public void testOperators() throws ScriptException {
        frege.eval("infix 1 `³`");
        frege.eval("(x³) = x^3");
        final Object actual = frege.eval("(2³)");
        final Object expected = 8;
        assertEquals(expected, actual);
    }

    @Test
    public void testImportOperators() throws ScriptException {
        frege.eval("import Data.Monoid");
        frege.eval("import frege.data.wrapper.Num");
        final Object actual = frege.eval("Sum.unwrap $ Sum 1 <> Sum 0");
        final Object expected = 1;
        assertEquals(expected, actual);
    }

    @Test
    public void testTypeAnnotation() throws ScriptException {
        final Object actual = frege.eval("one + one :: Int");
        final Object expected = 2;
        assertEquals(expected, actual);
    }

    @Test
    public void testWhere() throws ScriptException {
        final Object actual = frege.eval("x + 3 where x = 5");
        final Object expected = 8;
        assertEquals(expected, actual);
    }

    @Test
    public void testLet() throws ScriptException {
        final Object actual = frege.eval("let x = 5 in x + 3");
        final Object expected = 8;
        assertEquals(expected, actual);
    }

    @Test
    public void testTickInName() throws ScriptException {
        frege.eval("conanOBrien' = \"It's a-me, Conan O'Brien!\"");
        final Object actual = frege.eval("conanOBrien'");
        final Object expected = "It's a-me, Conan O'Brien!";
        assertEquals(expected, actual);
    }

    @Test
    public void testRebinding() throws ScriptException {
        final Bindings bindings = frege.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("bar :: Integer", new BigInteger("12312332142343244"));
        final Object actual1 = frege.eval("bar + 3.big");
        final Object expected1 = new BigInteger("12312332142343247");
        bindings.put("bar :: String", "hello ");
        final Object actual2 = frege.eval("bar ++ \"world\"");
        final Object expected2 = "hello world";
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    public void testHelloWorld() throws ScriptException {
        frege.put("hello :: String", "world");
        frege.eval("println(hello)");
        frege.eval("world = \"Hello \" ++ hello ++ \"!\"");
        final Object world = frege.eval("world");
        Assertions.checkEquals("Hello world!", world);
    }

    @Test
    public void testHelloWorldCompiled() throws ScriptException {
        // compiled frege does not support bindings sadly
        Assertions.assertThrows(Undefined.class, () -> {
            final Compilable compilableFrege = (Compilable) frege;
            final Bindings bindings = frege.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("hello :: String", "world");
            final CompiledScript compiled = compilableFrege.compile("world = \"Hello \" ++ hello ++ \"!\"");
            final Object world = compiled.eval(bindings);
            Assertions.checkEquals("Hello world!", world);
        });
    }

    /**
     * https://stackoverflow.com/questions/19061552/is-there-a-downloadable-jar-for-the-frege-jsr223-integration
     */
    @Test
    public void testEngine() throws Exception {
        //Get the Frege Script Engine
        //Evaluate an expression
        System.out.println(frege.eval("show $ take 10 [2,4..]"));

        //Bind a value
        frege.eval("x=5");
        System.out.println(frege.eval("x"));

        //Pass some objects from host to scripting environment
        frege.put("foo::String", "I am foo");
        frege.put("bar::Integer", new BigInteger("1234567890123456789"));

        //Use the objects from host environment
        System.out.println(frege.eval("\"Hello World, \" ++ foo"));
        //        System.out.println(engine.eval("bar + big 5"));

        /*
         * Frege Script Engine is `Compilable` too. So scripts can be compiled and then executed later.
         */
        final Compilable compilableEngine = (Compilable) frege;
        final CompiledScript compiled = compilableEngine.compile("fib = 0 : 1 : zipWith (+) fib fib.tail");
        compiled.eval(); //Evaluate the compiled script
        //use compiled script
        System.out.println(frege.eval("show $ take 6 fib"));
    }

    /**
     * https://groups.google.com/g/frege-programming-language/c/Oxs6isJR6d0/m/KkNSYh-gAwAJ
     */
    @Test
    public void testHelloWorldComplex() throws Exception {
        // Get Frege Script Engine
        // Evaluate a simple expression
        System.out.println("Result of 1 + 2 from Frege: " + frege.eval("1 + 2"));

        // Define a frege function
        frege.eval("add x y = x + y");

        // Call the previously defined function
        final int sum = (Integer) frege.eval("add 5 3");
        System.out.println("Sum: " + sum);

        // Evaluate an expression that gives a Frege value
        final TList fregeList = (TList) frege.eval("filter even [1..10]");
        //        System.out
        //                .println("Showing Frege List from Java: " + forced(IShow_$lbrack$rbrack.show(IShow_Int.it, fregeList)));

        // Pass something from Java
        final List<Integer> javaList = Arrays.asList(1, 2, 3, 4, 5);
        frege.eval("import Java.Util(List)");
        frege.put("javaList :: Mutable s (List Int)", javaList);

        final String fregeListAsString = (String) frege
                .eval("show . IO.performUnsafe $ javaList.iterator >>= _.toList");
        System.out.println("Frege List from Java List as String: " + fregeListAsString);

        // Evaluate a module
        frege.eval("module hello.Hello where\n" + "fib = 0 : 1 : zipWith (+) fib (tail fib)");
        frege.eval("import hello.Hello");
        System.out.println("First 10 fibonacci numbers calculated from module: " + frege.eval("show . take 10 $ fib"));
    }
}