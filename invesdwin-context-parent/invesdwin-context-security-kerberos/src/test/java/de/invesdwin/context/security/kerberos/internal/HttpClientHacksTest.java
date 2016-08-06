package de.invesdwin.context.security.kerberos.internal;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class HttpClientHacksTest extends ATest {

    @Test
    public void testInjectProxy() {
        final HttpClient httpClient = HttpClientBuilder.create().build();
        Assertions.assertThat(HttpClientHacks.extractRequestConfig(httpClient).getProxy()).isNull();
        HttpClientHacks.injectProxy(httpClient, new Proxy(Type.HTTP, new InetSocketAddress("asdf.de", 1234)));
        Assertions.assertThat(HttpClientHacks.extractRequestConfig(httpClient).getProxy()).isNotNull();
    }

}
