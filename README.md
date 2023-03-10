# iterator-utils
<!--[![Maven Central](https://img.shields.io/maven-central/v/com.github.robtimus/iterator-utils)](https://search.maven.org/artifact/com.github.robtimus/iterator-utils)-->
[![Build Status](https://github.com/robtimus/iterator-utils/actions/workflows/build.yml/badge.svg)](https://github.com/robtimus/iterator-utils/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Aiterator-utils&metric=alert_status)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Aiterator-utils)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Aiterator-utils&metric=coverage)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Aiterator-utils)
[![Known Vulnerabilities](https://snyk.io/test/github/robtimus/iterator-utils/badge.svg)](https://snyk.io/test/github/robtimus/iterator-utils)

Provides classes and interfaces to help implement iterators. Below are some examples; for a full list, see the [API](https://robtimus.github.io/iterator-utils/apidocs/).

## LookaheadIterator

Class [LookaheadIterator](https://robtimus.github.io/iterator-utils/apidocs/com/github/robtimus/util/iterator/LookaheadIterator.html) is a base class that lets you easily implement iterators that need to calculate the next value in order to let `hasNext()` return whether or not there is a next value.

## StreamLikeIterator

Interface [StreamLikeIterator](https://robtimus.github.io/iterator-utils/apidocs/com/github/robtimus/util/iterator/StreamLikeIterator.html) extends `Iterator` to add several `Stream` operations. Unlike streams, instances of `StreamLikeIterator` support removal if a) the original iterator does, and b) no intermediate step removes support for removal (e.g., `flatMap`).

## IteratorUtils

Class [IteratorUtils](https://robtimus.github.io/iterator-utils/apidocs/com/github/robtimus/util/iterator/IteratorUtils.html) provides several utility methods. These include:

* `singletonIterator` to create an (unmodifiable) iterator containing only a single element.
* `unmodifiableIterator` to create an unmodifiable wrapper around an iterator.
* methods to wrap an existing iterator to add functionality of `Stream`. This allows single operations to be used without having to use `StreamLikeIterator`.
* methods to create a `Stream` for an iterator.
* methods to create a single iterator backed by several other iterators or iterables.
