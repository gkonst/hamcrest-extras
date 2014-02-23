/***********************************************************************************
 *
 * Copyright 2013 by Round, LLC.
 *
 * All rights reserved.
 *
 * No part of this document may be reproduced or transmitted
 * in any form or by any means, electronic, mechanical,
 * photocopying, recording, or otherwise, without prior
 * written permission of Round, LLC.
 *
 ************************************************************************************/

package com.gkonst.hamcrest.matchers.guava;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Provides matchers for the {@link Optional} values.
 *
 * @param <T> type of value in optional
 * @see Optional
 */
public class IsOptional<T> extends TypeSafeMatcher<Optional<? extends T>> {

    private final boolean someExpected;
    private final Optional<T> expected;
    private final Optional<Matcher<T>> matcher;

    private IsOptional(boolean someExpected) {
        this.someExpected = someExpected;
        this.expected = Optional.absent();
        this.matcher = Optional.absent();
    }

    private IsOptional(T value) {
        this.someExpected = true;
        this.expected = Optional.of(value);
        this.matcher = Optional.absent();
    }

    private IsOptional(Matcher<T> matcher) {
        this.someExpected = true;
        this.expected = Optional.absent();
        this.matcher = Optional.of(matcher);
    }

    @Override
    public void describeTo(Description description) {
        if (!someExpected) {
            description.appendText("<Absent>");
        } else if (expected.isPresent()) {
            description.appendValue(expected);
        } else if (matcher.isPresent()) {
            description.appendText("a present value matching ");
            matcher.get().describeTo(description);
        } else {
            description.appendText("<Present>");
        }
    }

    @Override
    public boolean matchesSafely(Optional<? extends T> item) {
        if (!someExpected) {
            return !item.isPresent();
        } else if (expected.isPresent()) {
            return item.isPresent() && Objects.equal(item.get(), expected.get());
        } else if (matcher.isPresent()) {
            return item.isPresent() && matcher.get().matches(item.get());
        } else {
            return item.isPresent();
        }
    }

    /**
     * Checks that the passed Optional is not present.
     */
    @Factory
    public static IsOptional<Object> isAbsent() {
        return new IsOptional<>(false);
    }

    /**
     * Checks that the passed Optional is present.
     */
    @Factory
    public static IsOptional<Object> isPresent() {
        return new IsOptional<>(true);
    }

    /**
     * Checks that the passed Optional is Some and contains value matches {@code value} based on {@code Objects.equal}.
     *
     * @see Objects#equal(Object, Object)
     */
    @Factory
    public static <T> IsOptional<T> isValue(T value) {
        return new IsOptional<>(value);
    }

    /**
     * Checks that the passed Option is Some and contains value matches {@code value} based on given matcher.
     */
    @Factory
    public static <T> IsOptional<T> isValue(Matcher<T> matcher) {
        return new IsOptional<>(matcher);
    }
}
