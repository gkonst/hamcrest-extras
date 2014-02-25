package com.github.gkonst.hamcrest.matchers.io;

import org.hamcrest.StringDescription;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;

import static com.github.gkonst.hamcrest.matchers.io.IsUrlExists.anExistingUrl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class IsUrlExistsTest {
    @Test
    public void shouldMatchFileUrls() throws Exception {
        // given
        final URL matched = new URL("file://" + System.getProperty("user.home"));
        final URL mismatched = new URL("file:///abc/abc");

        // when & then
        assertThat(matched, anExistingUrl());
        assertThat(mismatched, not(anExistingUrl()));
    }

    @Test
    public void shouldMatchHttpUrls() throws Exception {
        // given
        final URL matched = new URL("http://google.com/");
        final URL mismatchedWithWrongHost = new URL("http://abr.abr");
        final URL mismatchedWithWrongPath = new URL("http://google.com/abr/abr");

        // when & then
        assertThat(matched, anExistingUrl());
        assertThat(mismatchedWithWrongHost, not(anExistingUrl()));
        assertThat(mismatchedWithWrongPath, not(anExistingUrl()));
        assertThat(new IsUrlExists().toString(), equalTo("an existent url"));
    }

    @Test
    public void shouldMatchHttpUrlsWithRedirects() throws Exception {
        // given
        final URL matchedIfRedirects = new URL("http://google.com/ru");

        // when & then
        assertThat(matchedIfRedirects, anExistingUrl());
        assertThat(matchedIfRedirects, not(anExistingUrl().withoutRedirects()));
    }

    @Test(dataProvider = "mismatches")
    public void shouldDescribeMismatch(String url, String expected, String was) throws Exception {
        // given
        final URL mismatched = new URL(url);
        final StringDescription expectedDescription = new StringDescription();
        final StringDescription wasDescription = new StringDescription();
        final IsUrlExists sut = new IsUrlExists();

        // when
        sut.matchesSafely(mismatched);
        sut.describeTo(expectedDescription);
        sut.describeMismatch(mismatched, wasDescription);

        // then
        assertThat("expectedDescription", expectedDescription.toString(), equalTo(expected));
        assertThat("wasDescription", wasDescription.toString(), equalTo(was));
    }

    @DataProvider
    private static Object[][] mismatches() {
        return new Object[][]{
                {"file:///abc/abc", "an existent url", "was FileNotFoundException: /abc/abc (No such file or directory)"},
                {"http://abr.abr", "an existent url with response code 2xx", "was UnknownHostException: abr.abr"},
                {"http://google.com/abr/abr", "an existent url with response code 2xx", "was unexpected response code 404"}
        };
    }
}
