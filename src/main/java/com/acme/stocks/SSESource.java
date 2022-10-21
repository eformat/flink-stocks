package com.acme.stocks;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.net.ssl.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.CertificateException;
import java.util.List;

public class SSESource extends RichSourceFunction<String> {

    private String url = "https://quote-generator-quote-generator.apps.openshift-411-x24tf.demo.redhatlabs.dev/quotes/stream"; //https://flask-stocks-flask-stocks.apps.openshift-411-x24tf.demo.redhatlabs.dev";
    private final Logger logger;

    public SSESource(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        OkHttpClient client = getUnsafeOkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();

        // Create a request and connect using the standard headers for SSE endpoints
        Request request = requestBuilder
                .url(url)
                .header("Accept-Encoding", "")
                .header("Accept", "text/event-stream")
                .header("Cache-Control", "no-cache")
                .build();
        logger.info("SSESource Request created to: " + url);
        EventSourceListener listener = new EventSourceSender(sourceContext, logger, null);
        final EventSource eventSource = EventSources.createFactory(client).newEventSource(request, listener);
        logger.info("SSESource connected");
        try {
            long startTime = System.currentTimeMillis();
            // while we are connected and running we need to hold this thread and report messages received if that option is enabled.
            // SSE events are sent via a callback in another thread
            Thread.sleep(10000000);

        } catch (InterruptedException e) {
            logger.error("Sleep timer interrupted");
        }
        eventSource.cancel();
        logger.info("SSESource Stopping event source");
    }

    @Override
    public void cancel() {

    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final class EventSourceSender extends EventSourceListener {
        final Logger logger;
        final SourceContext<String> sourceContext;
        final List<String> collectTypes;

        public EventSourceSender(SourceContext<String> sourceContext, Logger logger, List<String> collectTypes) {
            this.sourceContext = sourceContext;
            this.logger = logger;
            this.collectTypes = collectTypes;
        }

        @Override
        public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
            logger.info("SSESource open");
        }

        @Override
        public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
            if (collectTypes == null || collectTypes.contains(type)) {
                sourceContext.collect(data);
            }
        }

        @Override
        public void onClosed(@NotNull EventSource eventSource) {
            logger.info("SSESource closed");
        }

        @Override
        public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
            if (t != null) {
                logger.error("SSESource Error: " + t.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                logger.error("SSESource Error Trace: " + sw);
            }
        }
    }
}
