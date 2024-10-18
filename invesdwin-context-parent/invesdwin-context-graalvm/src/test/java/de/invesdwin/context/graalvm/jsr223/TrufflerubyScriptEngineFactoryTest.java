package de.invesdwin.context.graalvm.jsr223;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.ScriptException;

import org.graalvm.polyglot.Source.Builder;
import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class TrufflerubyScriptEngineFactoryTest extends ATest {

    @Test
    public void test() throws ScriptException {
        final PolyglotScriptEngineFactory factory = new PolyglotScriptEngineFactory("ruby") {
            @Override
            public Builder customizeSourceBuilder(final Builder builder) {
                return super.customizeSourceBuilder(builder).interactive(true);
            }
        };
        final PolyglotScriptEngine engine = factory.getScriptEngine();
        engine.put("hello", 1);
        engine.eval("world = 2");
        final Integer result = (Integer) engine.eval("hello+world");
        Assertions.assertThat(result).isEqualTo(3);
    }

}
