package com.github.gkonst.hamcrest.matchers.bean;

import com.google.common.collect.ImmutableList;
import org.hamcrest.StringDescription;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.gkonst.hamcrest.matchers.bean.TheSameAs.theSameAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class TheSameAsTest {

    private static class Pojo {
        private final String one;
        private final int two;
        private final List<String> three;

        private Pojo(String one, int two, List<String> three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }
    }

    @Test
    public void shouldMatchObjectsWithFields() throws Exception {
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), theSameAs(new Pojo("one", 2, ImmutableList.of("a"))));
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), not(theSameAs(new Pojo("two", 2, ImmutableList.of("a")))));
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), not(theSameAs(new Pojo("one", 2, ImmutableList.of("b")))));
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), not(theSameAs(new Pojo("one", 2, ImmutableList.of("b", "c")))));
    }

    @Test
    public void shouldMatchObjectsWithFieldsExcludingSomeOfThem() throws Exception {
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), theSameAs(new Pojo("one", 2, ImmutableList.of("a"))).excluding("three"));
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), theSameAs(new Pojo("two", 2, ImmutableList.of("a"))).excluding("one"));
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), not(theSameAs(new Pojo("one", 2, ImmutableList.of("b"))).excluding("one")));
        assertThat(new Pojo("one", 2, ImmutableList.of("a")), theSameAs(new Pojo("one", 2, ImmutableList.of("b", "c"))).excluding("three"));
    }

    @Test
    public void shouldDescribeMismatchedFields() throws Exception {
        // given
        final StringDescription expectedDescription = new StringDescription();
        final StringDescription wasDescription = new StringDescription();
        final Pojo was = new Pojo("one", 2, ImmutableList.of("a"));
        final Pojo expected = new Pojo("two", 2, ImmutableList.of("a"));
        final TheSameAs<Pojo> matcher = theSameAs(expected);
        matcher.matchesSafely(was);

        // when
        matcher.describeTo(expectedDescription);
        matcher.describeMismatchSafely(was, wasDescription);

        // then
        assertThat("expectedDescription", expectedDescription.toString(), equalTo("the same as Pojo(one=\"two\")"));
        assertThat("wasDescription", wasDescription.toString(), equalTo("was Pojo(one=\"one\")"));
    }

    @Test
    public void shouldDescribeMismatchedFieldsIfTwoFieldsMismatched() throws Exception {
        // given
        final StringDescription expectedDescription = new StringDescription();
        final StringDescription wasDescription = new StringDescription();
        final Pojo was = new Pojo("one", 2, ImmutableList.of("a"));
        final Pojo expected = new Pojo("two", 3, ImmutableList.of("a"));
        final TheSameAs<Pojo> matcher = theSameAs(expected);
        matcher.matchesSafely(was);

        // when
        matcher.describeTo(expectedDescription);
        matcher.describeMismatchSafely(was, wasDescription);

        // then
        assertThat("expectedDescription", expectedDescription.toString(), equalTo("the same as Pojo(one=\"two\",two=<3>)"));
        assertThat("wasDescription", wasDescription.toString(), equalTo("was Pojo(one=\"one\",two=<2>)"));
    }
}
