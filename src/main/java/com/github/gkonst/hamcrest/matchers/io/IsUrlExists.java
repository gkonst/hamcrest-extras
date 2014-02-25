package com.github.gkonst.hamcrest.matchers.io;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Provides matcher for matching {@link URL} existence.
 * Should work for all url connections. For HTTP connections also checks response code (2xx should be) using HEAD request.
 */
public class IsUrlExists extends TypeSafeMatcher<URL> {

    private static final String HTTP_REQUEST_METHOD = "HEAD";
    private boolean followRedirects = true;
    private String error;
    private boolean httpMatch = false;

    @Override
    public boolean matchesSafely(final URL url) {
        try {
            final URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
                return chechHttpUrl((HttpURLConnection) connection);
            } else {
                connection.connect();
                return true;
            }

        } catch (IOException e) {
            error = e.getClass().getSimpleName() + ": " + e.getMessage();
            return false;
        }
    }

    private boolean chechHttpUrl(HttpURLConnection connection) throws IOException {
        httpMatch = true;
        connection.setInstanceFollowRedirects(followRedirects);
        connection.setRequestMethod(HTTP_REQUEST_METHOD);
        final int responseCode = connection.getResponseCode();
        if (String.valueOf(responseCode).startsWith("2")) {
            return true;
        } else {
            error = "unexpected response code " + responseCode;
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("an existent url");
        if (httpMatch) {
            description.appendText(" with response code 2xx");
        }
    }

    @Override
    protected void describeMismatchSafely(URL item, Description mismatchDescription) {
        mismatchDescription.appendText("was ")
                .appendText(error);
    }

    /**
     * Turns off redirect following.
     */
    public IsUrlExists withoutRedirects() {
        followRedirects = false;
        return this;
    }

    /**
     * Checks that the target url exists.
     * Should work for all url connections. For HTTP connections also checks response code (2xx should be) using HEAD request.
     */
    @Factory
    public static IsUrlExists anExistingUrl() {
        return new IsUrlExists();
    }
}
