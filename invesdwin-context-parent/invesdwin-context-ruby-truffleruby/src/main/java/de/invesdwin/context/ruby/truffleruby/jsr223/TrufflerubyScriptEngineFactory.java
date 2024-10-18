package de.invesdwin.context.ruby.truffleruby.jsr223;

import javax.annotation.concurrent.Immutable;

import org.graalvm.polyglot.Source;

import de.invesdwin.context.graalvm.jsr223.PolyglotScriptEngineFactory;

@Immutable
public final class TrufflerubyScriptEngineFactory extends PolyglotScriptEngineFactory {

    public static final TrufflerubyScriptEngineFactory INSTANCE = new TrufflerubyScriptEngineFactory();

    public TrufflerubyScriptEngineFactory() {
        super("ruby");
    }

    /**
     * required to keep local variables: https://github.com/oracle/truffleruby/issues/1695
     */
    @Override
    public Source.Builder customizeSourceBuilder(final Source.Builder builder) {
        return super.customizeSourceBuilder(builder).interactive(true);
    }
}
