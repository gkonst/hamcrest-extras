package com.github.gkonst.hamcrest.matchers.bean;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Provides matcher, which compares bean fields using reflection.
 *
 * @param <T> type of value in value
 */
public class TheSameAs<T> extends TypeSafeMatcher<T> {

    private final T expected;
    private final List<Mismatch> mismatched = new ArrayList<>();
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
        mismatchDescription.appendText("was ");
        describe(mismatchDescription, false);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("the same as ");
        describe(description, true);
    }

    private void describe(Description description, boolean expectedOrWas) {
        description.appendText(expected.getClass().getSimpleName())
                .appendText("(");
        for (Iterator<Mismatch> iterator = mismatched.iterator(); iterator.hasNext();
                ) {
            Mismatch mismatch = iterator.next();
            description.appendText(mismatch.name)
                    .appendText("=")
                    .appendValue(expectedOrWas ? mismatch.expected : mismatch.was);
            if (iterator.hasNext()) {
                description.appendText(",");
            }
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
        return new TheSameAs<>(expected, new HashSet<>(asList(fields)));
    }

    private static Object readField(Field field, Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Compares field values of the given objects.
     * Uses reflection to work with fields and {@link Objects#equals(Object, Object)} to compare field values.
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
                final Object wasValue = readField(field, was);
                final Object expectedValue = readField(field, expected);
                if (!excluded.contains(field.getName()) && !Objects.equals(wasValue, expectedValue)) {
                    mismatched.add(new Mismatch(field.getName(), expectedValue, wasValue));
                }
            }
        }
    }

    private static class Mismatch {
        private final String name;
        private final Object expected;
        private final Object was;

        private Mismatch(String name, Object expected, Object was) {
            this.name = name;
            this.expected = expected;
            this.was = was;
        }
    }
}
