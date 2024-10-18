package de.invesdwin.context.graalvm.jsr223;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class EspressoScriptEngineFactoryTest extends ATest {

    @Test
    public void test() {
        final PolyglotScriptEngineFactory factory = new PolyglotScriptEngineFactory("java");
        final ScriptEngine engine = factory.getScriptEngine();
        final ScriptException scriptException = Assertions.assertThrows(ScriptException.class,
                () -> engine.eval("1+1"));
        Assertions.assertThat(scriptException.getMessage()).contains("Espresso cannot evaluate Java sources directly");
    }

}
