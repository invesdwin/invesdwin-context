package de.invesdwin.context.graalvm.jsr223;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.ScriptException;

import org.graalvm.polyglot.Source;
import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class TrufflerubyScriptEngineFactoryTest extends ATest {

    @Test
    public void test() throws ScriptException {
        final PolyglotScriptEngineFactory factory = new PolyglotScriptEngineFactory("ruby") {
            @Override
            public Source.Builder customizeSourceBuilder(final Source.Builder builder) {
                return super.customizeSourceBuilder(builder).interactive(true);
            }
        };
        final PolyglotScriptEngine engine = factory.getScriptEngine();
        engine.put("hello", 1);
        engine.eval("world = 2");
        final Integer result = (Integer) engine.eval("hello+world");
        Assertions.assertThat(result).isEqualTo(3);

        engine.put("greeting", "hello world");
        final String type = (String) engine.eval("greeting.class.to_s");
        Assertions.assertThat(type).isEqualTo("Polyglot::ForeignString");

        engine.eval("greetingStr = \"hello world\"");
        final String typeStr = (String) engine.eval("greetingStr.class.to_s");
        Assertions.assertThat(typeStr).isEqualTo("String");
    }

}
