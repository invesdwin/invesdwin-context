package de.invesdwin.context.ruby.truffleruby.jsr223;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.graalvm.jsr223.PolyglotScriptEngineFactory;

@Immutable
public final class TrufflerubyScriptEngineFactory extends PolyglotScriptEngineFactory {

    public static final TrufflerubyScriptEngineFactory INSTANCE = new TrufflerubyScriptEngineFactory();

    public TrufflerubyScriptEngineFactory() {
        super("ruby");
    }
}
