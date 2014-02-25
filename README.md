hamcrest-extras
===============

Custom Hamcrest matchers for guava, beans, etc.

[![Build Status](https://travis-ci.org/gkonst/hamcrest-extras.png?branch=master)](https://travis-ci.org/gkonst/hamcrest-extras)[![Coverage Status](https://coveralls.io/repos/gkonst/hamcrest-extras/badge.png?branch=master)](https://coveralls.io/r/gkonst/hamcrest-extras?branch=master)

## Usage

The static factory methods for all matchers are generated to `com.github.gkonst.hamcrest.matchers.Matchers` class for easy access.

## Contents

The package contains the following matchers:

* Guava `IsOptional` matchers
 - `isAbsent()` - matches that the target Optional is not present.
 - `isPresent()` - matches that the target Optional is present.
 - `isValue(T value)` - matches that the target Optional is Some and contains value equals given value.
 - `isValue(Matcher<T> matcher)` - matches that the target Optional is Some and contains value matches given matcher.
* Bean `TheSameIs` matcher
 - `theSameIs` - matches that the target bean has the same field values
* IO matchers
 - `anExistingUrl` - matches that the target url exists