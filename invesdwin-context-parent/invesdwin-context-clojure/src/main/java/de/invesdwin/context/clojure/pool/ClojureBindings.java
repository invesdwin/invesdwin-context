package de.invesdwin.context.clojure.pool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;

import clojure.lang.MapEntry;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

@NotThreadSafe
public class ClojureBindings implements Bindings {

    private static final String CORE_NS = "clojure.core";
    private static final String USER_NS = "user";

    public ClojureBindings() {
        final Var nameSpace = RT.var(CORE_NS, "*ns*");
        Var.pushThreadBindings(RT.map(nameSpace, nameSpace.get()));
        RT.var(CORE_NS, "in-ns").invoke(Symbol.intern(USER_NS));
        RT.var(CORE_NS, "refer").invoke(Symbol.intern(CORE_NS));
    }

    @Override
    public int size() {
        return Var.getThreadBindings().count();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(final Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(final Object value) {
        return map().containsValue(value);
    }

    @Override
    public Object get(final Object keyObject) {
        String key = (String) keyObject;
        final int dot = key.lastIndexOf('.');
        final String nameSpace;
        if (dot < 0) {
            nameSpace = USER_NS;
        } else {
            nameSpace = key.substring(0, dot);
            key = key.substring(dot + 1);
        }
        try {
            return RT.var(nameSpace, key).get();
        } catch (final Error e) {
            return null;
        }
    }

    private Object get(final String nameSpace, final String key) {
        return RT.var(nameSpace, key);
    }

    @Override
    public Object put(final String name, final Object value) {
        final int dot = name.lastIndexOf('.');
        final String nameSpace, key;
        if (dot < 0) {
            nameSpace = USER_NS;
            key = name;
        } else {
            nameSpace = name.substring(0, dot);
            key = name.substring(dot + 1);
        }
        final Object result = get(nameSpace, key);
        try {
            final Var var = RT.var(nameSpace, key, null);
            var.setDynamic();
            Var.pushThreadBindings(RT.map(var, value));
        } catch (final Error e) {
            // ignore
        }
        return result;
    }

    @Override
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> toMerge) {
        for (final Entry<? extends String, ? extends Object> entry : toMerge.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return map().keySet();
    }

    @Override
    public Collection<Object> values() {
        return map().values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map().entrySet();
    }

    // -- Helper methods --

    private static Map<String, Object> map() {
        final Map<String, Object> map = new HashMap<String, Object>();

        final Namespace ns = Namespace.find(Symbol.intern(null, USER_NS));
        for (final Object el : ns.getMappings()) {
            final MapEntry entry = (MapEntry) el;
            final Symbol key = (Symbol) entry.key();
            // NB: Unfortunately, we cannot simply write:
            //   final Object value = Var.intern(ns, key).get();
            // because it issues a warning for already-existing variables.
            // So instead, we replicate some of its internals here.
            final Object valAt = ns.getMappings().valAt(key);
            final Var valVar = valAt instanceof Var ? ((Var) valAt) : null;
            if (valVar == null) {
                continue; // skip non-variables
            }
            if (valVar.ns != ns) {
                continue; // skip non-user vars
            }
            if (!valVar.isBound()) {
                continue; // skip unbound vars
            }
            map.put(key.getName(), valVar.get());
        }

        return map;
    }
}