package com.twitter.internal.network.whiskey;

import android.support.annotation.Nullable;

import java.io.InputStream;
import java.net.CookieHandler;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;

public class Request {
    private final URL url;
    private final Request.Method method;
    private final Headers headers;
    private final ByteBuffer[] bodyData;
    private final InputStream bodyStream;
    private final CookieHandler cookieHandler;
    private final TimeUnit timeoutUnit;
    private final TimeUnit discretionaryUnit;
    private final double priority;
    private final long discretionaryTimeout;
    private final long timeout;
    private final int maxRedirects;
    private final boolean idempotent;

    Request(
        URL url,
        Method method,
        Headers headers,
        ByteBuffer[] bodyData,
        InputStream bodyStream,
        double priority,
        CookieHandler cookieHandler,
        long discretionaryTimeout,
        TimeUnit discretionaryUnit,
        boolean idempotent,
        int maxRedirects,
        long timeout,
        TimeUnit timeoutUnit
    ) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.bodyData = bodyData;
        this.bodyStream = bodyData == null ? bodyStream : null;
        this.priority = priority;
        this.cookieHandler = cookieHandler;
        this.discretionaryTimeout = discretionaryTimeout;
        this.discretionaryUnit = discretionaryUnit;
        this.idempotent = idempotent;
        this.maxRedirects = maxRedirects;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public URL getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    public Headers getHeaders() {
        return headers;
    }

    public ByteBuffer[] getBodyData() {
        return bodyData;
    }

    public InputStream getBodyStream() {
        return bodyStream;
    }

    public CookieHandler getCookieHandler() {
        return cookieHandler;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public double getPriority() {
        return priority;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    /**
     * HTTP Request Method as specified in RFC 2616
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html
     */
    public static enum Method {
        //      (name, idempotent, bodySupported, cacheable)
        OPTIONS ("OPTIONS", true, true, false),
        GET     ("GET", true, false, true),
        HEAD    ("HEAD", true, false, false), // may invalidate existing cache entries
        POST    ("POST", false, true, true),
        PUT     ("PUT", true, true, false),
        DELETE  ("DELETE", true, false, false), // may invalidate existing cache entries
        TRACE   ("TRACE", true, true, false);

        private final String method;
        private final boolean idempotent;
        private final boolean bodySupported;
        private final boolean cacheable;

        Method(String method, boolean idempotent, boolean bodySupported, boolean cacheable) {
            this.method = method;
            this.idempotent = idempotent;
            this.bodySupported = bodySupported;
            this.cacheable = cacheable;
        }

        public String toString() {
            return method;
        }

        @Nullable
        public static Method fromString(String name) {
            Method method;
            try {
                method = valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                method = null;
            }
            return method;
        }

        public boolean isIdempotent() {
            return idempotent;
        }

        public boolean isBodySupported() {
            return bodySupported;
        }

        public boolean isCacheable() {
            return cacheable;
        }
    }

    /**
     * HTTP-compatible protocol
     */
    public static enum Protocol {
        HTTP_1_0 ("http/1.0"),
        HTTP_1_1 ("http/1.1"),
        HTTP_2_0 ("http/2"),
        SPDY_2   ("spdy/2"),
        SPDY_3   ("spdy/3"),
        SPDY_3_1 ("spdy/3.1");

        private final String name;
        private Protocol(String name) { this.name = name; }

        @Override public String toString() { return name; }

        @Nullable
        public static Protocol fromString(String name) {
            Protocol protocol;
            try {
                protocol = valueOf(name.toUpperCase().replaceAll("[-/.]", "_"));
            } catch (IllegalArgumentException e) {
                protocol = null;
            }
            return protocol;
        }
    }
}
