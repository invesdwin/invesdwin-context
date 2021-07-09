package de.invesdwin.context.integration.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import javax.annotation.concurrent.Immutable;

import org.springframework.util.SocketUtils;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
public final class NetworkUtil extends SocketUtils {

    private NetworkUtil() {}

    /**
     * Identifies the local ip of the computer in the local network.
     * 
     * If there is no local ip, just any ip of any local interface is returned.
     * 
     * @see <a href=
     *      "http://www.java-tips.org/java-se-tips/java.net/how-to-detect-ip-address-and-name-of-host-machine-without-
     *      using-socket- p r o g r a . h t m l > S o u r c e < / a >
     */
    public static InetAddress getLocalAddress() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException e) {
            throw Err.process(e);
        }

        InetAddress remoteFallback = null;
        while (netInterfaces.hasMoreElements()) {
            final NetworkInterface ni = netInterfaces.nextElement();
            final Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                final InetAddress addr = address.nextElement();
                if (!addr.isLoopbackAddress() && !(addr.getHostAddress().indexOf(":") > -1)) {
                    if (addr.isSiteLocalAddress()) {
                        return addr;
                    } else {
                        try {
                            if (addr.isReachable(
                                    ContextProperties.DEFAULT_NETWORK_TIMEOUT.intValue(FTimeUnit.MILLISECONDS))) {
                                remoteFallback = addr;
                            }
                        } catch (final IOException e) { //SUPPRESS CHECKSTYLE empty
                            //ignore
                        }
                    }
                }
            }
        }
        return remoteFallback;
    }

    public static List<InetAddress> getLocalAddresses() {
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException e) {
            throw Err.process(e);
        }

        final List<InetAddress> localAddresses = new ArrayList<InetAddress>();
        while (netInterfaces.hasMoreElements()) {
            final NetworkInterface ni = netInterfaces.nextElement();
            final Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                final InetAddress addr = address.nextElement();
                if (!addr.isLoopbackAddress() && !(addr.getHostAddress().indexOf(":") > -1)) {
                    if (addr.isSiteLocalAddress()) {
                        localAddresses.add(addr);
                    } else {
                        try {
                            if (addr.isReachable(
                                    ContextProperties.DEFAULT_NETWORK_TIMEOUT.intValue(FTimeUnit.MILLISECONDS))) {
                                localAddresses.add(addr);
                            }
                        } catch (final IOException e) { //SUPPRESS CHECKSTYLE empty
                            //ignore
                        }
                    }
                }
            }
        }
        return localAddresses;
    }

    /**
     * Identifies the external ip of the computer in the internet.
     * 
     * @see <a href="http://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java">Source</a>
     */
    public static InetAddress getExternalAddress() {
        try {
            return InternetCheckHelper.getExternalIp();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static boolean waitIfInternetNotAvailable() throws InterruptedException {
        return waitIfInternetNotAvailable(true);
    }

    /**
     * Returns true if the internet was down.
     */
    public static boolean waitIfInternetNotAvailable(final boolean allowCache) throws InterruptedException {
        return InternetCheckHelper.waitIfInternetNotAvailable(allowCache);
    }

    /**
     * http://stackoverflow.com/questions/7348711/recommended-way-to-get-hostname-in-java
     */
    public static String getHostname() {
        return Strings.trim(determineHostname());
    }

    private static String determineHostname() {
        String hostname = System.getenv("COMPUTERNAME");
        if (Strings.isNotBlank(hostname)) {
            return hostname;
        }
        hostname = System.getenv("HOSTNAME");
        if (Strings.isNotBlank(hostname)) {
            return hostname;
        }
        hostname = readHostnameExec("hostname");
        if (Strings.isNotBlank(hostname)) {
            return hostname;
        }
        final File file = new File("/etc/hostname");
        hostname = readHostnameFile(file);
        if (Strings.isNotBlank(hostname)) {
            return hostname;
        }
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            //worst case fallback
            hostname = "localhost";
        }
        return hostname;
    }

    private static String readHostnameFile(final File file) {
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                return readHostnameStream(in);
            } catch (final FileNotFoundException e) {
                return null;
            } catch (final IOException e) {
                return null;
            }
        }
        return null;
    }

    private static String readHostnameExec(final String execCommand) {
        try {
            final Process proc = Runtime.getRuntime().exec(execCommand);
            try (InputStream stream = proc.getInputStream()) {
                return readHostnameStream(stream);
            }
        } catch (final IOException e) {
            return null;
        }
    }

    private static String readHostnameStream(final InputStream stream) {
        try (@SuppressWarnings("resource")
        Scanner s = new Scanner(stream).useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        }
    }

}
