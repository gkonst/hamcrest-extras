package com.gkonst.hamcrest.matchers.bean;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.gkonst.hamcrest.matchers.bean.TheSameAs.theSameAs;
import static org.hamcrest.MatcherAssert.assertThat;
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
}
