package de.invesdwin.context.integration.script.callback;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.norva.beanpath.impl.clazz.BeanClassType;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.ADecimal;

@NotThreadSafe
public class ReflectiveScriptTaskCallback implements IScriptTaskCallback {

    private static final ALoadingCache<MethodInfoKey, MethodInfo> KEY_METHODINFO = new ALoadingCache<MethodInfoKey, MethodInfo>() {
        @Override
        protected MethodInfo loadValue(final MethodInfoKey key) {
            return new MethodInfo(key.getProviderClass(), key.getMethodName(), key.getParameterCount());
        }

        @Override
        protected boolean isHighConcurrency() {
            return true;
        }

        @Override
        protected Integer getInitialMaximumSize() {
            return 1000;
        }
    };
    private final Object provider;

    public ReflectiveScriptTaskCallback(final Object provider) {
        this.provider = provider;
    }

    @Override
    public void invoke(final String methodName, final IScriptTaskParameters parameters,
            final IScriptTaskReturns returns) {
        KEY_METHODINFO.get(new MethodInfoKey(provider.getClass(), methodName, parameters.size()))
                .invoke(provider, parameters, returns);
    }

    private static final class MethodInfoKey {
        private final Class<?> providerClass;
        private final String methodName;
        private final int parameterCount;
        private final int hashCode;

        private MethodInfoKey(final Class<?> providerClass, final String methodName, final int parameterCount) {
            this.providerClass = providerClass;
            this.methodName = methodName;
            this.parameterCount = parameterCount;
            this.hashCode = Objects.hashCode(MethodInfoKey.class, providerClass, methodName, parameterCount);
        }

        public Class<?> getProviderClass() {
            return providerClass;
        }

        public String getMethodName() {
            return methodName;
        }

        public int getParameterCount() {
            return parameterCount;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof MethodInfoKey) {
                final MethodInfoKey cObj = (MethodInfoKey) obj;
                return Objects.equals(providerClass, cObj.providerClass) && Objects.equals(methodName, cObj.methodName)
                        && Integers.equals(parameterCount, cObj.parameterCount);
            }
            return super.equals(obj);
        }

    }

    private static final class MethodInfo {

        private final Class<?> providerClass;
        private final String methodName;
        private final int parameterCount;
        private final Function<IScriptTaskParameters, Object>[] parameterFunctions;
        private final Method method;
        private MethodHandle methodHandle;
        private final BiConsumer<IScriptTaskReturns, Object> returnFunction;

        @SuppressWarnings("unchecked")
        private MethodInfo(final Class<?> providerClass, final String methodName, final int parameterCount) {
            this.providerClass = providerClass;
            this.methodName = methodName;
            this.parameterCount = parameterCount;
            this.method = Reflections.findMethodByName(providerClass, methodName, parameterCount);
            if (method == null) {
                throw new IllegalArgumentException(
                        "Method not found: " + providerClass.getName() + "." + methodName + "[" + parameterCount + "]");
            }
            this.returnFunction = newReturnFunction();
            this.parameterFunctions = new Function[parameterCount];
            for (int i = 0; i < parameterCount; i++) {
                parameterFunctions[i] = newParameterFunction(i);
            }
            try {
                methodHandle = MethodHandles.lookup().unreflect(method);
            } catch (final IllegalAccessException e) {
                throw new IllegalArgumentException("Method not public: " + providerClass.getName() + "." + methodName
                        + "[" + parameterCount + "]: " + method, e);
            }
        }

        public void invoke(final Object provider, final IScriptTaskParameters parameters,
                final IScriptTaskReturns returns) {
            final Object[] args = new Object[parameterCount + 1];
            args[0] = provider;
            for (int i = 0; i < parameterCount; i++) {
                if (parameters.isNotNull(i)) {
                    args[i + 1] = parameterFunctions[i].apply(parameters);
                }
            }
            try {
                final Object returnValue = methodHandle.invokeWithArguments(args);
                if (returnValue == null) {
                    returns.returnNull();
                } else {
                    returnFunction.accept(returns, returnValue);
                }
            } catch (final Throwable e) {
                throw Throwables.propagate(e);
            }
        }

        private Function<IScriptTaskParameters, Object> newParameterFunction(final int index) {
            BeanClassType type = new BeanClassType(method.getParameterTypes()[index],
                    method.getGenericParameterTypes()[index]);
            if (type.isArray()) {
                type = Reflections.determineGenercType(type);
                if (type.isArray()) {
                    //matrix
                    type = Reflections.determineGenercType(type);
                    final Function<IScriptTaskParameters, Object> function = newParameterFunctionMatrix(index, type);
                    if (function != null) {
                        return function;
                    }
                } else {
                    //vector
                    final Function<IScriptTaskParameters, Object> function = newParameterFunctionVector(index, type);
                    if (function != null) {
                        return function;
                    }
                }
            } else if (type.isInstanceOf(List.class)) {
                type = Reflections.determineGenercType(type);
                if (type.isInstanceOf(List.class)) {
                    //matrix
                    type = Reflections.determineGenercType(type);
                    final Function<IScriptTaskParameters, Object> function = newParameterFunctionMatrixAsList(index,
                            type);
                    if (function != null) {
                        return function;
                    }
                } else {
                    //vector
                    final Function<IScriptTaskParameters, Object> function = newParameterFunctionVectorAsList(index,
                            type);
                    if (function != null) {
                        return function;
                    }
                }
            } else {
                final Function<IScriptTaskParameters, Object> function = newParameterFunctionValue(index, type);
                if (function != null) {
                    return function;
                }
            }
            throw new IllegalArgumentException("Unsupported method " + index + ". parameter type: "
                    + providerClass.getName() + "." + methodName + "[" + parameterCount + "]: " + method);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Function<IScriptTaskParameters, Object> newParameterFunctionMatrix(final int index,
                final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (p) -> p.getByteMatrix(index);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (p) -> p.getCharacterMatrix(index);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (p) -> p.getStringMatrix(index);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (p) -> p.getFloatMatrix(index);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (p) -> p.getDoubleMatrix(index);
            } else if (type.isInstanceOf(ADecimal.class)) {
                final ADecimal converter = determineDecimalConverter(type);
                return (p) -> p.getDecimalMatrix(index, converter);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (p) -> p.getShortMatrix(index);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (p) -> p.getIntegerMatrix(index);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (p) -> p.getLongMatrix(index);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (p) -> p.getBooleanMatrix(index);
            } else {
                return null;
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Function<IScriptTaskParameters, Object> newParameterFunctionVector(final int index,
                final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (p) -> p.getByteVector(index);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (p) -> p.getCharacterVector(index);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (p) -> p.getStringVector(index);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (p) -> p.getFloatVector(index);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (p) -> p.getDoubleVector(index);
            } else if (type.isInstanceOf(ADecimal.class)) {
                final ADecimal converter = determineDecimalConverter(type);
                return (p) -> p.getDecimalVector(index, converter);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (p) -> p.getShortVector(index);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (p) -> p.getIntegerVector(index);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (p) -> p.getLongVector(index);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (p) -> p.getBooleanVector(index);
            } else {
                return null;
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private Function<IScriptTaskParameters, Object> newParameterFunctionMatrixAsList(final int index,
                final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (p) -> p.getByteMatrixAsList(index);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (p) -> p.getCharacterMatrixAsList(index);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (p) -> p.getStringMatrixAsList(index);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (p) -> p.getFloatMatrixAsList(index);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (p) -> p.getDoubleMatrixAsList(index);
            } else if (type.isInstanceOf(ADecimal.class)) {
                final ADecimal converter = determineDecimalConverter(type);
                return (p) -> p.getDecimalMatrixAsList(index, converter);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (p) -> p.getShortMatrixAsList(index);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (p) -> p.getIntegerMatrixAsList(index);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (p) -> p.getLongMatrixAsList(index);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (p) -> p.getBooleanMatrixAsList(index);
            } else {
                return null;
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Function<IScriptTaskParameters, Object> newParameterFunctionVectorAsList(final int index,
                final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (p) -> p.getByteVectorAsList(index);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (p) -> p.getCharacterVectorAsList(index);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (p) -> p.getStringVectorAsList(index);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (p) -> p.getFloatVectorAsList(index);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (p) -> p.getDoubleVectorAsList(index);
            } else if (type.isInstanceOf(ADecimal.class)) {
                final ADecimal converter = determineDecimalConverter(type);
                return (p) -> p.getDecimalVectorAsList(index, converter);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (p) -> p.getShortVectorAsList(index);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (p) -> p.getIntegerVectorAsList(index);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (p) -> p.getLongVectorAsList(index);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (p) -> p.getBooleanVectorAsList(index);
            } else {
                return null;
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Function<IScriptTaskParameters, Object> newParameterFunctionValue(final int index,
                final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (p) -> p.getByte(index);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (p) -> p.getCharacter(index);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (p) -> p.getString(index);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (p) -> p.getFloat(index);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (p) -> p.getDouble(index);
            } else if (type.isInstanceOf(ADecimal.class)) {
                final ADecimal converter = determineDecimalConverter(type);
                return (p) -> p.getDecimal(index, converter);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (p) -> p.getShort(index);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (p) -> p.getInteger(index);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (p) -> p.getLong(index);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (p) -> p.getBoolean(index);
            } else {
                return null;
            }
        }

        public ADecimal<?> determineDecimalConverter(final BeanClassType type) {
            final ADecimal<?> converter = (ADecimal<?>) Reflections.staticField("ZERO")
                    .ofType(type.getType())
                    .in(type.getType())
                    .get();
            return converter;
        }

        private BiConsumer<IScriptTaskReturns, Object> newReturnFunction() {
            BeanClassType type = new BeanClassType(method.getReturnType(), method.getGenericReturnType());
            if (type.isArray()) {
                type = Reflections.determineGenercType(type);
                if (type.isArray()) {
                    //matrix
                    type = Reflections.determineGenercType(type);
                    final BiConsumer<IScriptTaskReturns, Object> function = newReturnFunctionMatrix(type);
                    if (function != null) {
                        return function;
                    }
                } else {
                    //vector
                    final BiConsumer<IScriptTaskReturns, Object> function = newReturnFunctionVector(type);
                    if (function != null) {
                        return function;
                    }
                }
            } else if (type.isInstanceOf(List.class)) {
                type = Reflections.determineGenercType(type);
                if (type.isInstanceOf(List.class)) {
                    //matrix
                    type = Reflections.determineGenercType(type);
                    final BiConsumer<IScriptTaskReturns, Object> function = newReturnFunctionMatrixAsList(type);
                    if (function != null) {
                        return function;
                    }
                } else {
                    //vector
                    final BiConsumer<IScriptTaskReturns, Object> function = newReturnFunctionVectorAsList(type);
                    if (function != null) {
                        return function;
                    }
                }
            } else {
                final BiConsumer<IScriptTaskReturns, Object> function = newReturnFunctionValue(type);
                if (function != null) {
                    return function;
                }
            }
            throw new IllegalArgumentException("Unsupported method return type: " + providerClass.getName() + "."
                    + methodName + "[" + parameterCount + "]: " + method);
        }

        private BiConsumer<IScriptTaskReturns, Object> newReturnFunctionMatrix(final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (r, o) -> r.returnByteMatrix((byte[][]) o);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (r, o) -> r.returnCharacterMatrix((char[][]) o);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (r, o) -> r.returnStringMatrix((String[][]) o);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (r, o) -> r.returnFloatMatrix((float[][]) o);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (r, o) -> r.returnDoubleMatrix((double[][]) o);
            } else if (type.isInstanceOf(ADecimal.class)) {
                return (r, o) -> r.returnDecimalMatrix((ADecimal[][]) o);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (r, o) -> r.returnShortMatrix((short[][]) o);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (r, o) -> r.returnIntegerMatrix((int[][]) o);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (r, o) -> r.returnLongMatrix((long[][]) o);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (r, o) -> r.returnBooleanMatrix((boolean[][]) o);
            } else {
                return null;
            }
        }

        private BiConsumer<IScriptTaskReturns, Object> newReturnFunctionVector(final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (r, o) -> r.returnByteVector((byte[]) o);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (r, o) -> r.returnCharacterVector((char[]) o);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (r, o) -> r.returnStringVector((String[]) o);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (r, o) -> r.returnFloatVector((float[]) o);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (r, o) -> r.returnDoubleVector((double[]) o);
            } else if (type.isInstanceOf(ADecimal.class)) {
                return (r, o) -> r.returnDecimalVector((ADecimal[]) o);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (r, o) -> r.returnShortVector((short[]) o);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (r, o) -> r.returnIntegerVector((int[]) o);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (r, o) -> r.returnLongVector((long[]) o);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (r, o) -> r.returnBooleanVector((boolean[]) o);
            } else {
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private BiConsumer<IScriptTaskReturns, Object> newReturnFunctionMatrixAsList(final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (r, o) -> r.returnByteMatrixAsList((List<List<Byte>>) o);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (r, o) -> r.returnCharacterMatrixAsList((List<List<Character>>) o);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (r, o) -> r.returnStringMatrixAsList((List<List<String>>) o);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (r, o) -> r.returnFloatMatrixAsList((List<List<Float>>) o);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (r, o) -> r.returnDoubleMatrixAsList((List<List<Double>>) o);
            } else if (type.isInstanceOf(ADecimal.class)) {
                return (r, o) -> r.returnDecimalMatrixAsList((List<List<ADecimal<?>>>) o);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (r, o) -> r.returnShortMatrixAsList((List<List<Short>>) o);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (r, o) -> r.returnIntegerMatrixAsList((List<List<Integer>>) o);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (r, o) -> r.returnLongMatrixAsList((List<List<Long>>) o);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (r, o) -> r.returnBooleanMatrixAsList((List<List<Boolean>>) o);
            } else {
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private BiConsumer<IScriptTaskReturns, Object> newReturnFunctionVectorAsList(final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (r, o) -> r.returnByteVectorAsList((List<Byte>) o);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (r, o) -> r.returnCharacterVectorAsList((List<Character>) o);
            } else if (type.isInstanceOf(CharSequence.class)) {
                return (r, o) -> r.returnStringVectorAsList((List<String>) o);
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (r, o) -> r.returnFloatVectorAsList((List<Float>) o);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (r, o) -> r.returnDoubleVectorAsList((List<Double>) o);
            } else if (type.isInstanceOf(ADecimal.class)) {
                return (r, o) -> r.returnDecimalVectorAsList((List<ADecimal<?>>) o);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (r, o) -> r.returnShortVectorAsList((List<Short>) o);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (r, o) -> r.returnIntegerVectorAsList((List<Integer>) o);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (r, o) -> r.returnLongVectorAsList((List<Long>) o);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (r, o) -> r.returnBooleanVectorAsList((List<Boolean>) o);
            } else {
                return null;
            }
        }

        private BiConsumer<IScriptTaskReturns, Object> newReturnFunctionValue(final BeanClassType type) {
            if (type.isInstanceOf(byte.class) || type.isInstanceOf(Byte.class)) {
                return (r, o) -> r.returnByte((byte) o);
            } else if (type.isInstanceOf(char.class) || type.isInstanceOf(Character.class)) {
                return (r, o) -> r.returnCharacter((char) o);
            } else if (type.isInstanceOf(CharSequence.class)) {
                if (Reflections.getAnnotation(ReturnExpression.class) != null) {
                    return (r, o) -> r.returnExpression(String.valueOf(o));
                } else {
                    return (r, o) -> r.returnString(String.valueOf(o));
                }
            } else if (type.isInstanceOf(float.class) || type.isInstanceOf(Float.class)) {
                return (r, o) -> r.returnFloat((float) o);
            } else if (type.isInstanceOf(double.class) || type.isInstanceOf(Double.class)) {
                return (r, o) -> r.returnDouble((double) o);
            } else if (type.isInstanceOf(ADecimal.class)) {
                return (r, o) -> r.returnDecimal((ADecimal<?>) o);
            } else if (type.isInstanceOf(short.class) || type.isInstanceOf(Short.class)) {
                return (r, o) -> r.returnShort((short) o);
            } else if (type.isInstanceOf(int.class) || type.isInstanceOf(Integer.class)) {
                return (r, o) -> r.returnInteger((int) o);
            } else if (type.isInstanceOf(long.class) || type.isInstanceOf(Long.class)) {
                return (r, o) -> r.returnLong((long) o);
            } else if (type.isInstanceOf(boolean.class) || type.isInstanceOf(Boolean.class)) {
                return (r, o) -> r.returnBoolean((boolean) o);
            } else if (type.isInstanceOf(void.class) || type.isInstanceOf(Void.class)) {
                return (r, o) -> r.returnNull();
            } else {
                return null;
            }
        }
    }

}
