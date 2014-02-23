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

package kg.hamcrest.matchers.bean;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides matcher, which compares bean fields using reflection.
 *
 * @param <T> type of value in value
 */
public class TheSameAs<T> extends TypeSafeMatcher<T> {

    private final T expected;
    private final Map<String, Pair> mismatched = new HashMap<>();
    private final Set<String> excluded;

    private TheSameAs(T expected) {
        this.expected = expected;
        this.excluded = Collections.emptySet();
    }

    private TheSameAs(T expected, Set<String> excluded) {
        this.expected = expected;
        this.excluded = excluded;
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText("was ")
                .appendText(expected.getClass().getSimpleName())
                .appendText("(");
        for (Map.Entry<String, Pair> entry : mismatched.entrySet()) {
            mismatchDescription.appendText(entry.getKey())
                    .appendText("=")
                    .appendValue(entry.getValue().was);
        }
        mismatchDescription.appendText(")");
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("the same as ")
                .appendText(expected.getClass().getSimpleName())
                .appendText("(");
        for (Map.Entry<String, Pair> entry : mismatched.entrySet()) {
            description.appendText(entry.getKey())
                    .appendText("=")
                    .appendValue(entry.getValue().expected);
        }
        description.appendText(")");
    }

    @Override
    public boolean matchesSafely(T item) {
        new Comparison(expected, item).compare();
        return mismatched.isEmpty();
    }

    /**
     * Specify fields to be excluded from comparison.
     *
     * @param fields fields names
     */
    public TheSameAs<T> excluding(String... fields) {
        return new TheSameAs<>(expected, ImmutableSet.copyOf(fields));
    }

    private static Object readField(Field field, Object target) {
        try {
            return FieldUtils.readField(field, target, true);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Compares field values of the given objects.
     * Uses reflection to work with fields and {@link Objects#equal(Object, Object)} to compare field values.
     * {@link #excluding(String...)} method can be used to exclude some fields from comparison.
     */
    @Factory
    public static <T> TheSameAs<T> theSameAs(T expected) {
        return new TheSameAs<>(expected);
    }

    private class Comparison {
        private final Object expected;
        private final Object was;

        private Comparison(Object expected, Object was) {
            this.expected = expected;
            this.was = was;
        }

        public void compare() {
            for (Field field : was.getClass().getDeclaredFields()) {
                final java.lang.Object wasValue = readField(field, was);
                final java.lang.Object expectedValue = readField(field, expected);
                if (!excluded.contains(field.getName()) && !Objects.equal(wasValue, expectedValue)) {
                    mismatched.put(field.getName(), new Pair(expectedValue, wasValue));
                }
            }
        }
    }

    private static class Pair {
        private final Object expected;
        private final Object was;

        private Pair(Object expected, Object was) {
            this.expected = expected;
            this.was = was;
        }
    }
}
