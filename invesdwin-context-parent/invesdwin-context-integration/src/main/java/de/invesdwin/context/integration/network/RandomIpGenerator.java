package de.invesdwin.context.integration.network;

import java.net.InetAddress;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.math.random.PseudoRandomGenerators;

/**
 * @see <a href="http://de.wikipedia.org/wiki/IP-Adresse">IP-Segments</a>
 * 
 * @author subes
 * 
 */
@Immutable
public final class RandomIpGenerator {

    private RandomIpGenerator() {}

    /**
     * Returns a random ip without private classes.
     */
    public static InetAddress getRandomIp() {
        int one = getRandomByte();
        while (isPrivateClassA(one)) {
            one = getRandomByte();
        }
        int two = getRandomByte();
        while (isPrivateClassB(one, two) || isPrivateClassC(one, two)) {
            two = getRandomByte();
        }
        final int three = getRandomByte();
        int four = getRandomByte();
        while (isBroadcast(one, two, three, four)) {
            four = getRandomByte();
        }

        return Addresses.asAddress(one + "." + two + "." + three + "." + four);
    }

    private static int getRandomByte() {
        return PseudoRandomGenerators.getThreadLocalPseudoRandom().nextInt(0, 256); //Generates from 0 to 255
    }

    private static boolean isPrivateClassA(final int one) {
        return one == 10 || one == 0 || one == 127;
    }

    private static boolean isPrivateClassB(final int one, final int two) {
        return one == 172 && two >= 16 && two <= 31;
    }

    private static boolean isPrivateClassC(final int one, final int two) {
        return one == 192 && two == 168 || one == 169 && two == 254;
    }

    private static boolean isBroadcast(final int one, final int two, final int three, final int four) {
        return one == 255 && two == 255 && three == 255 && four == 255;
    }
}
