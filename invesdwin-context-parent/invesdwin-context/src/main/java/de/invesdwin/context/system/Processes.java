package de.invesdwin.context.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public final class Processes {

    private static final String OS;
    private static final boolean WIN;
    private static final boolean NIX;

    static {
        OS = new SystemProperties().getString("os.name").toLowerCase();
        WIN = OS.indexOf("win") >= 0;
        NIX = OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0;
    }

    private Processes() {}

    public static boolean isProcessIdRunning(final String pidStr) {
        String command = null;
        if (WIN) {
            command = "cmd /c tasklist /FI \"PID eq " + pidStr + "\"";
        } else if (NIX) {
            command = "ps -p " + pidStr;
        } else {
            throw UnknownArgumentException.newInstance(String.class, OS);
        }
        return isProcessIdRunning(pidStr, command);
    }

    private static boolean isProcessIdRunning(final String pid, final String command) {
        try {
            final Runtime rt = Runtime.getRuntime();
            final Process pr = rt.exec(command);

            final InputStreamReader isReader = new InputStreamReader(pr.getInputStream());
            try (BufferedReader bReader = new BufferedReader(isReader)) {
                String strLine = null;
                while ((strLine = bReader.readLine()) != null) {
                    if (strLine.contains(" " + pid + " ")) {
                        return true;
                    }
                }
            }

            return false;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProcessId() {
        return DynamicInstrumentationProperties.getProcessId();
    }

    public static String getProcessName() {
        return DynamicInstrumentationProperties.getProcessName();
    }

}
