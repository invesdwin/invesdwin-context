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

    private RandomIpGenerator() {
    }

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
        return PseudoRandomGenerators.getThreadLocalPseudoRandom().nextInt(0, 255); //Generates from 0 to 255
    }

    private static boolean isPrivateClassA(final int eins) {
        return eins == 10 || eins == 0 || eins == 127;
    }

    private static boolean isPrivateClassB(final int eins, final int zwei) {
        return eins == 172 && zwei >= 16 && zwei <= 31;
    }

    private static boolean isPrivateClassC(final int eins, final int zwei) {
        return eins == 192 && zwei == 168 || eins == 169 && zwei == 254;
    }

    private static boolean isBroadcast(final int eins, final int zwei, final int drei, final int vier) {
        return eins == 255 && zwei == 255 && drei == 255 && vier == 255;
    }
}
