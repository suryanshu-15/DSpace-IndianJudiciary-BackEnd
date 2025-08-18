package org.dspace.app.rest.diracai.util;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.net.HttpURLConnection;
import java.io.IOException;

public class InsecureRestTemplateFactory {

    public static RestTemplate getInsecureRestTemplate() throws Exception {
        // Create trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        // Return a RestTemplate using a custom ClientHttpRequestFactory
        return new RestTemplate(new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                if (connection instanceof HttpsURLConnection httpsConn) {
                    httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
                    httpsConn.setHostnameVerifier((hostname, session) -> true);
                }
                super.prepareConnection(connection, httpMethod);
            }
        });
    }
}
