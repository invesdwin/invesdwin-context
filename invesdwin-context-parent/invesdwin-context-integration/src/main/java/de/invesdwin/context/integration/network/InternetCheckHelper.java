package de.invesdwin.context.integration.network;

import java.net.InetAddress;
import java.net.URI;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
final class InternetCheckHelper {

    public static final Duration INTERNET_CHECK_FREQUENCY = new Duration(30, FTimeUnit.SECONDS);
    private static final Log LOG = new Log(InternetCheckHelper.class);

    @GuardedBy("InternetCheckHelper.class")
    private static Instant lastWarned = Instant.DUMMY;
    @GuardedBy("InternetCheckHelper.class")
    private static boolean checkRunning;
    @GuardedBy("InternetCheckHelper.class")
    private static boolean lastCheckResult;
    @GuardedBy("InternetCheckHelper.class")
    private static Instant lastCheck = Instant.DUMMY;
    private static volatile InetAddress lastCheckIp;

    private InternetCheckHelper() {}

    public static InetAddress getExternalIp() throws InterruptedException {
        isInternetAvailable(true);
        return lastCheckIp;
    }

    public static boolean waitIfInternetNotAvailable(final boolean allowCache) throws InterruptedException {
        final boolean warNichtVerfuegbar = !InternetCheckHelper.isInternetAvailable(allowCache);
        if (warNichtVerfuegbar) {
            boolean warneEinmalig = true;
            do {
                Threads.throwIfInterrupted();
                boolean warnen = false;
                synchronized (InternetCheckHelper.class) {
                    if (new Duration(lastWarned).isGreaterThan(INTERNET_CHECK_FREQUENCY)) {
                        lastWarned = new Instant();
                        warnen = true;
                    } else {
                        warneEinmalig = false;
                    }
                }
                if (warneEinmalig && warnen) {
                    warneEinmalig = false;
                    LOG.warn("Internet not available, retrying every %s", InternetCheckHelper.INTERNET_CHECK_FREQUENCY);
                }
                INTERNET_CHECK_FREQUENCY.sleep();
            } while (!InternetCheckHelper.isInternetAvailable(true));
        }
        return warNichtVerfuegbar;
    }

    /**
     * Checks in the interval of INTERNET_CHECK_FREQUENCY. Waits instead of checking in parallel if another check is
     * currently running and returns the last result.
     */
    public static boolean isInternetAvailable(final boolean allowCache) throws InterruptedException {
        //Decide if checking or waiting for another check
        synchronized (InternetCheckHelper.class) {
            if (allowCache && lastCheck != Instant.DUMMY
                    && new Duration(lastCheck).isLessThan(INTERNET_CHECK_FREQUENCY)) {
                //only check in the given frequency
                return lastCheckResult;
            }
            if (checkRunning) {
                while (checkRunning) {
                    //already check running, thus wait for it
                    InternetCheckHelper.class.wait();
                }
                return lastCheckResult;
            } else {
                //no check running, thus start one
                checkRunning = true;
            }
        }
        //if not waiting, run check
        boolean internetAvailable = false;
        for (final URI uri : IntegrationProperties.INTERNET_CHECK_URIS) {
            final String result = URIs.connect(uri).download();
            if (Addresses.isIp(result)) {
                lastCheckIp = Addresses.asAddress(result);
                internetAvailable = true;
                break;
            }
        }
        //and publish the result
        synchronized (InternetCheckHelper.class) {
            lastCheck = new Instant();
            lastCheckResult = internetAvailable;
            checkRunning = false;
            InternetCheckHelper.class.notifyAll();
        }
        return internetAvailable;
    }
}
