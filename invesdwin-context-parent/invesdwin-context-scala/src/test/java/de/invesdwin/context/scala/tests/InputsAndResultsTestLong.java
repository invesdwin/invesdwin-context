package de.invesdwin.context.scala.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.scala.AScriptTaskScala;
import de.invesdwin.context.scala.IScriptTaskRunnerScala;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class InputsAndResultsTestLong {

    private final IScriptTaskRunnerScala runner;

    public InputsAndResultsTestLong(final IScriptTaskRunnerScala runner) {
        this.runner = runner;
    }

    public void testLong() {
        //putLong
        final long putLong = 123;

        //putLongVector
        final long[] putLongVector = new long[3];
        for (int i = 0; i < putLongVector.length; i++) {
            putLongVector[i] = Long.parseLong((i + 1) + "" + (i + 1));
        }

        //putLongVectorAsList
        final List<Long> putLongVectorAsList = Arrays.asList(ArrayUtils.toObject(putLongVector));

        //putLongMatrix
        final long[][] putLongMatrix = new long[4][];
        for (int row = 0; row < putLongMatrix.length; row++) {
            final long[] vector = new long[3];
            for (int col = 0; col < vector.length; col++) {
                vector[col] = Long.parseLong((row + 1) + "" + (col + 1));
            }
            putLongMatrix[row] = vector;
        }

        //putLongMatrixAsList
        final List<List<Long>> putLongMatrixAsList = new ArrayList<List<Long>>(putLongMatrix.length);
        for (final long[] vector : putLongMatrix) {
            putLongMatrixAsList.add(Arrays.asList(ArrayUtils.toObject(vector)));
        }

        new AScriptTaskScala<Void>() {

            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
                inputs.putLong("putLong", putLong);

                inputs.putLongVector("putLongVector", putLongVector);

                inputs.putLongVectorAsList("putLongVectorAsList", putLongVectorAsList);

                inputs.putLongMatrix("putLongMatrix", putLongMatrix);

                inputs.putLongMatrixAsList("putLongMatrixAsList", putLongMatrixAsList);
            }

            @Override
            public void executeScript(final IScriptTaskEngine engine) {
                engine.eval(new ClassPathResource(InputsAndResultsTestLong.class.getSimpleName() + ".js",
                        InputsAndResultsTestLong.class));
            }

            @Override
            public Void extractResults(final IScriptTaskResults results) {
                //getLong
                final Long getLong = results.getLong("getLong");
                Assertions.assertThat(putLong).isEqualTo(getLong);

                //getLongVector
                final long[] getLongVector = results.getLongVector("getLongVector");
                Assertions.assertThat(putLongVector).isEqualTo(getLongVector);

                //getLongVectorAsList
                final List<Long> getLongVectorAsList = results.getLongVectorAsList("getLongVectorAsList");
                Assertions.assertThat(putLongVectorAsList).isEqualTo(getLongVectorAsList);

                //getLongMatrix
                final long[][] getLongMatrix = results.getLongMatrix("getLongMatrix");
                Assertions.assertThat(putLongMatrix).isEqualTo(getLongMatrix);

                //getLongMatrixAsList
                final List<List<Long>> getLongMatrixAsList = results.getLongMatrixAsList("getLongMatrixAsList");
                Assertions.assertThat(putLongMatrixAsList).isEqualTo(getLongMatrixAsList);
                return null;
            }
        }.run(runner);
    }

}
