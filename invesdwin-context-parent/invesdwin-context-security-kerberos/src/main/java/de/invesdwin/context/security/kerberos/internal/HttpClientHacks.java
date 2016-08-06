package de.invesdwin.context.security.kerberos.internal;

import java.net.InetSocketAddress;
import java.net.Proxy;

import javax.annotation.concurrent.Immutable;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;

import de.invesdwin.util.lang.Reflections;

@Immutable
public final class HttpClientHacks {

    private static final String REQUEST_CONFIG_FIELD_NAME = "defaultConfig";

    private HttpClientHacks() {}

    public static void injectProxy(final HttpClient httpClient, final Proxy systemProxy) {
        //httpclient 4.3.5 got worse with its immutable design, so we have to hack our way around this...
        final RequestConfig originalRequestConfig = extractRequestConfig(httpClient);
        final InetSocketAddress addr = (InetSocketAddress) systemProxy.address();
        //            Assertions.assertThat(systemProxy.type()).isEqualTo(Type.HTTP);
        final RequestConfig newRequestConfig = RequestConfig.copy(originalRequestConfig)
                .setProxy(new HttpHost(addr.getHostName(), addr.getPort()))
                .build();
        Reflections.field(REQUEST_CONFIG_FIELD_NAME).ofType(RequestConfig.class).in(httpClient).set(newRequestConfig);
    }

    public static RequestConfig extractRequestConfig(final HttpClient httpClient) {
        return Reflections.field(REQUEST_CONFIG_FIELD_NAME).ofType(RequestConfig.class).in(httpClient).get();
    }

}
