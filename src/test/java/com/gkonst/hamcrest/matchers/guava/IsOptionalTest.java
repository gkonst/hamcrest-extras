package com.gkonst.hamcrest.matchers.guava;

import com.google.common.base.Optional;
import org.testng.annotations.Test;

import static com.gkonst.hamcrest.matchers.guava.IsOptional.isAbsent;
import static com.gkonst.hamcrest.matchers.guava.IsOptional.isPresent;
import static com.gkonst.hamcrest.matchers.guava.IsOptional.isValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class IsOptionalTest {
    @Test
    public void shouldMatchThatValueIsAbsent() throws Exception {
        assertThat(Optional.absent(), isAbsent());
        assertThat(Optional.of("foo"), not(isAbsent()));
        assertThat(isAbsent().toString(), equalTo("<Absent>"));
    }

    @Test
    public void shouldMatchThatValueIsPresent() throws Exception {
        assertThat(Optional.of("foo"), isPresent());
        assertThat(Optional.absent(), not(isPresent()));
        assertThat(isPresent().toString(), equalTo("<Present>"));
    }

    @Test
    public void shouldMatchValueUsingConcreteValue() throws Exception {
        assertThat(Optional.of("foo"), isValue("foo"));
        assertThat(Optional.of("bar"), not(isValue("foo")));
        assertThat(isValue("foo").toString(), equalTo("<Optional.of(foo)>"));
    }

    @Test
    public void shouldMatchValueUsingMatcher() throws Exception {
        assertThat(Optional.of("foo"), isValue(containsString("foo")));
        assertThat(Optional.of("bar"), not(isValue(containsString("foo"))));
        assertThat(isValue(containsString("foo")).toString(), equalTo("a present value matching a string containing \"foo\""));
    }
}

