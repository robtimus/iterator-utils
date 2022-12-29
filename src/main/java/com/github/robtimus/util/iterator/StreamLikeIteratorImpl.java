/*
 * StreamLikeIteratorImpl.java
 * Copyright 2022 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.util.iterator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;

final class StreamLikeIteratorImpl<E> implements StreamLikeIterator<E> {

    private final Iterator<E> delegate;

    StreamLikeIteratorImpl(Iterator<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        return delegate.next();
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        delegate.forEachRemaining(action);
    }

    @Override
    public StreamLikeIterator<E> filter(Predicate<? super E> predicate) {
        Iterator<E> newDelegate = IteratorUtils.filter(delegate, predicate);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public <R> StreamLikeIterator<R> map(Function<? super E, ? extends R> mapper) {
        Iterator<R> newDelegate = IteratorUtils.map(delegate, mapper);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public <R> StreamLikeIterator<R> flatMap(Function<? super E, ? extends Iterator<? extends R>> mapper) {
        Iterator<R> newDelegate = IteratorUtils.flatMap(delegate, mapper);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public StreamLikeIterator<E> distinct() {
        Iterator<E> newDelegate = IteratorUtils.distinct(delegate);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public StreamLikeIterator<E> peek(Consumer<? super E> action) {
        Iterator<E> newDelegate = IteratorUtils.peek(delegate, action);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public StreamLikeIterator<E> limit(long maxSize) {
        Iterator<E> newDelegate = IteratorUtils.limit(delegate, maxSize);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public StreamLikeIterator<E> skip(long n) {
        Iterator<E> newDelegate = IteratorUtils.skip(delegate, n);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public StreamLikeIterator<E> takeWhile(Predicate<? super E> predicate) {
        Iterator<E> newDelegate = IteratorUtils.takeWhile(delegate, predicate);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public StreamLikeIterator<E> dropWhile(Predicate<? super E> predicate) {
        Iterator<E> newDelegate = IteratorUtils.dropWhile(delegate, predicate);
        return new StreamLikeIteratorImpl<>(newDelegate);
    }

    @Override
    public E reduce(E identity, BinaryOperator<E> accumulator) {
        return IteratorUtils.reduce(delegate, identity, accumulator);
    }

    @Override
    public Optional<E> reduce(BinaryOperator<E> accumulator) {
        return IteratorUtils.reduce(delegate, accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator) {
        return IteratorUtils.reduce(delegate, identity, accumulator);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super E> accumulator) {
        return IteratorUtils.collect(delegate, supplier, accumulator);
    }

    @Override
    public <R, A> R collect(Collector<? super E, A, R> collector) {
        return IteratorUtils.collect(delegate, collector);
    }

    @Override
    public Optional<E> min(Comparator<? super E> comparator) {
        return IteratorUtils.min(delegate, comparator);
    }

    @Override
    public Optional<E> max(Comparator<? super E> comparator) {
        return IteratorUtils.max(delegate, comparator);
    }

    @Override
    public long count() {
        return IteratorUtils.count(delegate);
    }

    @Override
    public boolean anyMatch(Predicate<? super E> predicate) {
        return IteratorUtils.anyMatch(delegate, predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super E> predicate) {
        return IteratorUtils.allMatch(delegate, predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super E> predicate) {
        return IteratorUtils.noneMatch(delegate, predicate);
    }

    @Override
    public Optional<E> findFirst() {
        return IteratorUtils.findFirst(delegate);
    }
}
