/*
 * StreamLikeIterator.java
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * An {@link Iterator} that contains additional functionality similar to {@link Stream}.
 *
 * @author Rob Spoor
 * @apiNote Instances of this class support the {@link Iterator#remove()} operation if the original iterator does, and no intermediate operation of
 *          this class removes support for the operation.
 * @param <E> The element type.
 */
public interface StreamLikeIterator<E> extends Iterator<E> {

    /**
     * Returns an iterator that filters out elements of this iterator.
     *
     * @param predicate A predicate that determines whether or not elements should be included.
     * @return An iterator that filters out elements of this iterator.
     * @throws NullPointerException If the given predicate is {@code null}.
     * @see Stream#filter(Predicate)
     */
    StreamLikeIterator<E> filter(Predicate<? super E> predicate);

    /**
     * Returns an iterator that applies a function to the elements of this iterator.
     *
     * @param <R> The element type of the resulting iterator.
     * @param mapper The function to apply.
     * @return An iterator that applies the given function to the elements of this iterator.
     * @throws NullPointerException If the given function is {@code null}.
     * @see Stream#map(Function)
     */
    <R> StreamLikeIterator<R> map(Function<? super E, ? extends R> mapper);

    /**
     * Returns an iterator that replaces the elements of this iterator with the elements of a mapped iterator produced by applying a function to each
     * element.
     *
     * @apiNote Any iterator returned by a method of this interface after this call does <strong>not</strong> support the {@link Iterator#remove()}
     *          operation.
     * @param <R> The element type of the resulting iterator.
     * @param mapper The function to apply.
     * @return An iterator that applies the given function to the elements of this iterator.
     * @throws NullPointerException If the given function is {@code null}.
     * @see Stream#flatMap(Function)
     */
    <R> StreamLikeIterator<R> flatMap(Function<? super E, ? extends Iterator<? extends R>> mapper);

    /**
     * Returns an iterator that returns the distinct elements of this iterator (according to {@link Object#equals(Object)}).
     *
     * @apiNote If elements occur more than once in the original iterator, only the first occurrence will be returned by the returned iterator,
     *          and therefore only the first occurrence can be removed.
     * @return An iterator that returns the distinct elements of this iterator.
     * @see Stream#distinct()
     */
    StreamLikeIterator<E> distinct();

    /**
     * Returns an iterator that performs an additional action for each element of this iterator.
     *
     * @param action The action to perform.
     * @return An iterator that performs the given action for each element of this iterator.
     * @throws NullPointerException If the given action is {@code null}.
     * @see Stream#peek(Consumer)
     */
    StreamLikeIterator<E> peek(Consumer<? super E> action);

    /**
     * Returns an iterator that truncates this iterator.
     *
     * @param maxSize The maximum number of elements in the returned iterator.
     * @return An iterator that truncates this iterator.
     * @throws IllegalArgumentException If the given maximum number of elements is negative.
     * @see Stream#limit(long)
     */
    StreamLikeIterator<E> limit(long maxSize);

    /**
     * Returns an iterator that discards a number of elements at the start of this iterator.
     *
     * @param n The number of elements to discard.
     * @return An iterator that discards the first {@code n} elements of this iterator.
     * @throws IllegalArgumentException If the given number of elements is negative.
     * @see Stream#skip(long)
     */
    StreamLikeIterator<E> skip(long n);

    /**
     * Returns an iterator that discards elements of this iterator once an element matches a specific predicate.
     *
     * @param predicate The predicate that determines when elements are discarded.
     * @return An iterator that discards elements of this iterator once an element matches the given predicate.
     * @throws NullPointerException If the given predicate is {@code null}.
     */
    StreamLikeIterator<E> takeWhile(Predicate<? super E> predicate);

    /**
     * Returns an iterator that discards elements of this iterator until an element does not match a specific predicate.
     *
     * @param predicate The predicate that determines until when elements are discarded.
     * @return An iterator that discards elements of this iterator until an element does not match the given predicate.
     * @throws NullPointerException If the given predicate is {@code null}.
     */
    StreamLikeIterator<E> dropWhile(Predicate<? super E> predicate);

    /**
     * Performs a reduction on the elements of this iterator.
     *
     * @param identity The identity value for the accumulating function.
     * @param accumulator A function for combining two values.
     * @return The result of the reduction.
     * @throws NullPointerException If the given accumulator function is {@code null}.
     * @see Stream#reduce(Object, BinaryOperator)
     */
    E reduce(E identity, BinaryOperator<E> accumulator);

    /**
     * Performs a reduction on the elements of this iterator.
     *
     * @param accumulator A function for combining two values.
     * @return An {@link Optional} describing the result of the reduction, or {@link Optional#empty()} if this iterator has no elements.
     * @throws NullPointerException If the given accumulator function or the result of the reduction is {@code null}.
     * @see Stream#reduce(BinaryOperator)
     */
    Optional<E> reduce(BinaryOperator<E> accumulator);

    /**
     * Performs a reduction on the elements of this iterator.
     *
     * @param <U> The element type of the resulting iterator.
     * @param identity The identity value for the accumulating function.
     * @param accumulator A function for combining two values.
     * @return The result of the reduction.
     * @throws NullPointerException If the given accumulator function is {@code null}.
     * @see Stream#reduce(Object, BiFunction, BinaryOperator)
     */
    <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator);

    /**
     * Performs a reduction on the elements of this iterator.
     *
     * @param <R> The element type of the resulting iterator.
     * @param supplier A supplier for mutable result containers.
     * @param accumulator A function that folds elements into a result container.
     * @return The result of the reduction.
     * @throws NullPointerException If the given supplier or accumulator function is {@code null}.
     * @see Stream#collect(Supplier, BiConsumer, BiConsumer)
     */
    <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super E> accumulator);

    /**
     * Performs a reduction on the elements of this iterator.
     *
     * @param <R> The element type of the resulting iterator.
     * @param <A> The intermediate accumulation type of the collector.
     * @param collector The collector to use for the reduction.
     * @return The result of the reduction.
     * @throws NullPointerException If the given collector is {@code null}.
     * @see Stream#collect(Collector)
     */
    <R, A> R collect(Collector<? super E, A, R> collector);

    /**
     * Returns the minimum element of this iterator according to a specific comparator.
     *
     * @param comparator The comparator to use for comparing elements.
     * @return An {@link Optional} describing the minimum element, or {@link Optional#empty()} if this iterator has no elements.
     * @throws NullPointerException If the given comparator is {@code null}.
     * @see Stream#min(Comparator)
     */
    Optional<E> min(Comparator<? super E> comparator);

    /**
     * Returns the maximum element of this iterator according to a specific comparator.
     *
     * @param comparator The comparator to use for comparing elements.
     * @return An {@link Optional} describing the maximum element, or {@link Optional#empty()} if this iterator has no elements.
     * @throws NullPointerException If the given comparator is {@code null}.
     * @see Stream#max(Comparator)
     */
    Optional<E> max(Comparator<? super E> comparator);

    /**
     * Returns the number of elements of this iterator.
     *
     * @return The number of elements of this iterator.
     * @see Stream#count()
     */
    long count();

    /**
     * Returns whether or not at least one element of this iterator matches a specific predicate.
     *
     * @param predicate The predicate to apply to elements of this iterator.
     * @return {@code true} if at least one element of this iterator matches the given predicate, or {@code false} otherwise.
     * @throws NullPointerException If the given predicate is {@code null}.
     * @see Stream#anyMatch(Predicate)
     */
    boolean anyMatch(Predicate<? super E> predicate);

    /**
     * Returns whether or not all elements of this iterator match a specific predicate.
     *
     * @param predicate The predicate to apply to elements of this iterator.
     * @return {@code true} if all elements of this iterator match the given predicate, or {@code false} otherwise.
     * @throws NullPointerException If the given predicate is {@code null}.
     * @see Stream#allMatch(Predicate)
     */
    boolean allMatch(Predicate<? super E> predicate);

    /**
     * Returns whether or not no element of this iterator matches a specific predicate.
     *
     * @param predicate The predicate to apply to elements of this iterator.
     * @return {@code true} if no element of this iterator matches the given predicate, or {@code false} otherwise.
     * @throws NullPointerException If the given predicate is {@code null}.
     * @see Stream#noneMatch(Predicate)
     */
    boolean noneMatch(Predicate<? super E> predicate);

    /**
     * Returns the first element of this iterator.
     *
     * @return An {@link Optional} describing the first element, or {@link Optional#empty()} if this iterator has no elements.
     * @see Stream#findFirst()
     */
    Optional<E> findFirst();

    /**
     * Creates a new {@code StreamLikeIterator} backed by another iterator.
     *
     * @param <E> The element type.
     * @param iterator The backing iterator.
     * @return The created {@code StreamLikeIterator}
     */
    static <E> StreamLikeIterator<E> backedBy(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);
        return new StreamLikeIteratorImpl<>(iterator);
    }
}
