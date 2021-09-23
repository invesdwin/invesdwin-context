package de.invesdwin.context.integration.persistentmap;

public interface IKeyMatcher<K> {

    boolean matches(K key);

}
