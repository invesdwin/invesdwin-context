package de.invesdwin.context.clojure.tests.callback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.clojure.AScriptTaskClojure;
import de.invesdwin.context.clojure.IScriptTaskRunnerClojure;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.ReflectiveScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.ReturnExpression;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.UUIDs;
import de.invesdwin.util.math.decimal.Decimal;

@NotThreadSafe
public class SimpleCallbackTest {

    private static final Map<String, String> UUID_SECRET = new ConcurrentHashMap<>();

    private final IScriptTaskRunnerClojure runner;
    private int voidMethodCalled;

    public SimpleCallbackTest(final IScriptTaskRunnerClojure runner) {
        this.runner = runner;
    }

    public static String getSecretStatic(final String uuid) {
        return UUID_SECRET.get(uuid);
    }

    public String getSecret(final String uuid) {
        return UUID_SECRET.get(uuid);
    }

    @ReturnExpression
    public String getSecretExpression(final String uuid) {
        return "(str \"secret\" \"123\")";
    }

    public void voidMethod() {
        voidMethodCalled++;
    }

    public double callManyParams(final boolean p1, final byte p2, final short p3, final char p4, final int p5,
            final long p6, final float p7, final double p8, final String p9, final Decimal p10) {
        return (p1 ? 1 : 0) + p2 + p3 + Double.parseDouble(String.valueOf(p4)) + p5 + p6 + p7 + p8 + p9.length()
                + p10.doubleValue();
    }

    @ReturnExpression
    public String callManyParamsExpression(final boolean p1, final byte p2, final short p3, final char p4, final int p5,
            final long p6, final float p7, final double p8, final String p9, final Decimal p10) {
        final StringBuilder expression = new StringBuilder();
        expression.append("(+ ");
        expression.append(p1 ? 1 : 0);
        expression.append(" (+ ");
        expression.append(p2);
        expression.append(" (+ ");
        expression.append(p3);
        expression.append(" (+ ");
        expression.append(Double.parseDouble(String.valueOf(p4)));
        expression.append(" (+ ");
        expression.append(p5);
        expression.append(" (+ ");
        expression.append(p6);
        expression.append(" (+ ");
        expression.append(p7);
        expression.append(" (+ ");
        expression.append(p8);
        expression.append(" (+ ");
        expression.append(p9.length());
        expression.append(" ");
        expression.append(p10.doubleValue());
        expression.append(")))))))))");
        return expression.toString();
    }

    @ReturnExpression
    public String callManyParamsExpressionMultiline(final boolean p1, final byte p2, final short p3, final char p4,
            final int p5, final long p6, final float p7, final double p8, final String p9, final Decimal p10) {
        final StringBuilder expression = new StringBuilder("(def value ");
        expression.append(p1 ? 1 : 0);
        expression.append(")\n (def value (+ value ");
        expression.append(p2);
        expression.append("))\n (def value (+ value ");
        expression.append(p3);
        expression.append("))\n (def value (+ value ");
        expression.append(Double.parseDouble(String.valueOf(p4)));
        expression.append("))\n (def value (+ value ");
        expression.append(p5);
        expression.append("))\n (def value (+ value ");
        expression.append(p6);
        expression.append("))\n (def value (+ value ");
        expression.append(p7);
        expression.append("))\n (def value (+ value ");
        expression.append(p8);
        expression.append("))\n (def value (+ value ");
        expression.append(p9.length());
        expression.append("))\n (def value (+ value ");
        expression.append(p10.doubleValue());
        expression.append(")) value");
        return expression.toString();
    }

    private String putManyParamsExpressionMultiline(final String variable, final boolean p1, final byte p2,
            final short p3, final char p4, final int p5, final long p6, final float p7, final double p8,
            final String p9, final Decimal p10) {
        final StringBuilder expression = new StringBuilder("(def " + variable + " ");
        expression.append(p1 ? 1 : 0);
        expression.append(")\n (def " + variable + " (+ " + variable + " ");
        expression.append(p2);
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(p3);
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(Double.parseDouble(String.valueOf(p4)));
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(p5);
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(p6);
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(p7);
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(p8);
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(p9.length());
        expression.append("))\n (def " + variable + " (+ " + variable + " ");
        expression.append(p10.doubleValue());
        expression.append("))");
        return expression.toString();
    }

    public void testSimpleCallback() {
        final String uuid = UUIDs.newPseudoRandomUUID();
        final String secret = "secret123";
        UUID_SECRET.put(uuid, secret);
        try {
            new AScriptTaskClojure<Void>() {

                @Override
                public IScriptTaskCallback getCallback() {
                    return new ReflectiveScriptTaskCallback(SimpleCallbackTest.this);
                }

                @Override
                public void populateInputs(final IScriptTaskInputs inputs) {
                    inputs.putString("putUuid", uuid);
                    inputs.putExpression("putManyParamsExpression", callManyParamsExpression(true, (byte) 2, (short) 3,
                            '4', 5, 6L, 7f, 8.0, "123456789", new Decimal("10")));
                    /*
                     * requires a nasty workaround because of the trailing ")" in the "(def ...)" of putExpression,
                     * somehow directly using eval with the expression does not work because it throws too many args for
                     * eval (11), only loading as a string works
                     */
                    inputs.putExpression("putManyParamsExpressionMultilineWrong",
                            "(load-string \""
                                    + putManyParamsExpressionMultiline("putManyParamsExpressionMultilineWrong", true,
                                            (byte) 2, (short) 3, '4', 5, 6L, 7f, 8.0, "123456789", new Decimal("10"))
                                    + " putManyParamsExpressionMultilineWrong" //
                                    + "\")");
                    inputs.getEngine()
                            .eval(putManyParamsExpressionMultiline("putManyParamsExpressionMultiline", true, (byte) 2,
                                    (short) 3, '4', 5, 6L, 7f, 8.0, "123456789", new Decimal("10")));
                }

                @Override
                public void executeScript(final IScriptTaskEngine engine) {
                    engine.eval(new ClassPathResource(SimpleCallbackTest.class.getSimpleName() + ".clj",
                            SimpleCallbackTest.class));
                }

                @Override
                public Void extractResults(final IScriptTaskResults results) {
                    final String getSecretStaticImport = results.getString("getSecretStaticImport");
                    Assertions.assertThat(getSecretStaticImport).isEqualTo(secret);

                    final String getSecretStaticCallback = results.getString("getSecretStaticCallback");
                    Assertions.assertThat(getSecretStaticCallback).isEqualTo(secret);

                    final String getSecretCallback = results.getString("getSecretCallback");
                    Assertions.assertThat(getSecretCallback).isEqualTo(secret);

                    final String getSecretExpressionCallback = results.getString("getSecretExpressionCallback");
                    Assertions.assertThat(getSecretExpressionCallback).isEqualTo(secret);

                    Assertions.assertThat(voidMethodCalled).isEqualTo(1);

                    final double getManyParamsExpression = results.getDouble("getManyParamsExpression");
                    Assertions.assertThat(getManyParamsExpression).isEqualTo(55.0);

                    final double getManyParamsExpressionMultilineWrong = results
                            .getDouble("getManyParamsExpressionMultilineWrong");
                    Assertions.assertThat(getManyParamsExpressionMultilineWrong).isEqualTo(55.0);

                    final double getManyParamsExpressionMultiline = results
                            .getDouble("getManyParamsExpressionMultiline");
                    Assertions.assertThat(getManyParamsExpressionMultiline).isEqualTo(55.0);
                    return null;
                }
            }.run(runner);
        } finally {
            UUID_SECRET.remove(uuid);
        }
    }

}
