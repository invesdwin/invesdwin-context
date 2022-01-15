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
import de.invesdwin.util.lang.UniqueNameGenerator;

/**
 * WARNING: this class should not be used directly, instead the thread local instance from WrappedClojureEngine should
 * be used. Else bindings will not be visible by scripts, causing issues like this:
 * https://ask.clojure.org/index.php/11482/unable-resolve-symbol-after-pushthreadbindings-second-thread
 */
@NotThreadSafe
public final class ClojureBindings implements Bindings {

    private static final UniqueNameGenerator NAMESPACE = new UniqueNameGenerator();

    private static final String CORE_NS = "clojure.core";
    private static final Symbol CORE_NS_INTERN = Symbol.intern(null, CORE_NS);

    private final String isolatedNamespace;
    private final Symbol isolatedNamespaceIntern;

    public ClojureBindings() {
        final Var nameSpace = RT.var(CORE_NS, "*ns*");
        Var.pushThreadBindings(RT.map(nameSpace, nameSpace.get()));
        this.isolatedNamespace = NAMESPACE.get(ClojureBindings.class.getSimpleName());
        this.isolatedNamespaceIntern = Symbol.intern(null, isolatedNamespace);
        //https://clojuredocs.org/clojure.core/in-ns
        RT.var(CORE_NS, "in-ns").invoke(isolatedNamespaceIntern);
        RT.var(CORE_NS, "refer").invoke(CORE_NS_INTERN);
    }

    public String getIsolatedNamespace() {
        return isolatedNamespace;
    }

    public Symbol getIsolatedNamespaceIntern() {
        return isolatedNamespaceIntern;
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
    public boolean containsKey(final Object keyObject) {
        String key = (String) keyObject;
        final int dot = key.lastIndexOf('.');
        final String nameSpace;
        if (dot < 0) {
            nameSpace = isolatedNamespace;
        } else {
            nameSpace = key.substring(0, dot);
            key = key.substring(dot + 1);
        }
        final Object valAt = RT.var(nameSpace, key);
        final Var valVar = valAt instanceof Var ? ((Var) valAt) : null;
        if (valVar == null) {
            return false;
        }
        if (!valVar.isBound()) {
            return false;
        }
        return true;
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
            nameSpace = isolatedNamespace;
        } else {
            nameSpace = key.substring(0, dot);
            key = key.substring(dot + 1);
        }
        final Object valAt = RT.var(nameSpace, key);
        final Var valVar = valAt instanceof Var ? ((Var) valAt) : null;
        if (valVar == null) {
            return null;
        }
        if (!valVar.isBound()) {
            return null;
        }
        return valVar.get();
    }

    private Object get(final String nameSpace, final String key) {
        return RT.var(nameSpace, key);
    }

    @Override
    public Object put(final String name, final Object value) {
        final int dot = name.lastIndexOf('.');
        final String nameSpace, key;
        if (dot < 0) {
            nameSpace = isolatedNamespace;
            key = name;
        } else {
            nameSpace = name.substring(0, dot);
            key = name.substring(dot + 1);
        }
        final Object result = get(nameSpace, key);
        final Var var = RT.var(nameSpace, key, null);
        var.setDynamic();
        Var.pushThreadBindings(RT.map(var, value));
        return result;
    }

    @Override
    public Object remove(final Object key) {
        RT.var(CORE_NS, "ns-unmap").invoke(isolatedNamespaceIntern, Symbol.intern(key.toString()));
        return null;
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> toMerge) {
        for (final Entry<? extends String, ? extends Object> entry : toMerge.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        final Symbol nsSymbol = isolatedNamespaceIntern;

        final Namespace ns = Namespace.find(nsSymbol);
        for (final Object el : ns.getMappings()) {
            final MapEntry entry = (MapEntry) el;
            final Symbol key = (Symbol) entry.key();
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
            RT.var(CORE_NS, "ns-unmap").invoke(nsSymbol, key);
        }
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

    private Map<String, Object> map() {
        final Map<String, Object> map = new HashMap<String, Object>();

        final Namespace ns = Namespace.find(isolatedNamespaceIntern);
        for (final Object el : ns.getMappings()) {
            final MapEntry entry = (MapEntry) el;
            final Symbol key = (Symbol) entry.key();
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