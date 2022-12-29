/*
 * IteratorUtils.java
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A utility class for {@link Iterator}s.
 *
 * @author Rob Spoor
 */
public final class IteratorUtils {

    private IteratorUtils() {
    }

    /**
     * Returns an iterator that returns a single element. This method is like using the iterator of {@link Collections#singleton(Object)}.
     *
     * @apiNote The returned iterator does <strong>not</strong> support the {@link Iterator#remove()} operation.
     * @param <E> The element type.
     * @param element The element to return.
     * @return An iterator that returns the given element.
     */
    public static <E> Iterator<E> singletonIterator(E element) {
        return new SingletonIterator<>(element);
    }

    /**
     * Returns an unmodifiable view of an iterator. Calling {@link Iterator#remove()} will result in an {@link UnsupportedOperationException}.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return an unmodifiable view of.
     * @return An unmodifiable view of the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     */
    public static <E> Iterator<E> unmodifiableIterator(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);

        return new UnmodifiableIterator<>(iterator);
    }

    /**
     * Returns an iterator that filters out elements of another iterator.
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     * @param <E> The element type.
     * @param iterator The iterator for which to filter out elements.
     * @param predicate A predicate that determines whether or not elements should be included.
     * @return An iterator that filters out elements of the given iterator.
     * @throws NullPointerException If the given iterator or predicate is {@code null}.
     * @see Stream#filter(Predicate)
     */
    public static <E> Iterator<E> filter(Iterator<E> iterator, Predicate<? super E> predicate) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(predicate);

        return new FilterIterator<>(iterator, predicate);
    }

    /**
     * Returns an iterator that applies a function to the elements of another iterator.
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     * @param <E> The element type of the input iterator.
     * @param <R> The element type of the resulting iterator.
     * @param iterator The iterator with the elements to apply the function to.
     * @param mapper The function to apply.
     * @return An iterator that applies the given function to the elements of the given iterator.
     * @throws NullPointerException If the given iterator or function is {@code null}.
     * @see Stream#map(Function)
     */
    public static <E, R> Iterator<R> map(Iterator<E> iterator, Function<? super E, ? extends R> mapper) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(mapper);

        return new MapIterator<>(iterator, mapper);
    }

    /**
     * Returns an iterator that replaces the elements of another iterator with the elements of a mapped iterator produced by applying a function to
     * each element.
     *
     * @apiNote The returned iterator does <strong>not</strong> support the {@link Iterator#remove()} operation.
     * @param <E> The element type of the input iterator.
     * @param <R> The element type of the resulting iterator.
     * @param iterator The iterator with the elements to apply the function to.
     * @param mapper The function to apply.
     * @return An iterator that applies the given function to the elements of the given iterator.
     * @throws NullPointerException If the given iterator or function is {@code null}.
     * @see Stream#flatMap(Function)
     */
    public static <E, R> Iterator<R> flatMap(Iterator<E> iterator, Function<? super E, ? extends Iterator<? extends R>> mapper) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(mapper);

        return new FlatMapIterator<>(iterator, mapper);
    }

    /**
     * Returns an iterator that returns the distinct elements of another iterator (according to {@link Object#equals(Object)}).
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     *          If elements occur more than once in the original iterator, only the first occurrence will be returned by the returned iterator,
     *          and therefore only the first occurrence can be removed.
     * @param <E> The element type.
     * @param iterator The element to return distinct elements of.
     * @return An iterator that returns the distinct elements of the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @see Stream#distinct()
     */
    public static <E> Iterator<E> distinct(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);

        return new DistinctIterator<>(iterator);
    }

    /**
     * Returns an iterator that performs an additional action for each element of another iterator.
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     * @param <E> The element type.
     * @param iterator The iterator with the element to perform an action for.
     * @param action The action to perform.
     * @return An iterator that performs the given action for each element of the given iterator.
     * @throws NullPointerException If the given iterator or action is {@code null}.
     * @see Stream#peek(Consumer)
     */
    public static <E> Iterator<E> peek(Iterator<E> iterator, Consumer<? super E> action) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(action);

        return new PeekIterator<>(iterator, action);
    }

    /**
     * Returns an iterator that truncates another iterator.
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     *          Discarded elements will not be returned by the returned iterator, and therefore cannot be removed.
     * @param <E> The element type.
     * @param iterator The iterator to truncate.
     * @param maxSize The maximum number of elements in the returned iterator.
     * @return An iterator that truncates the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @throws IllegalArgumentException If the given maximum number of elements is negative.
     * @see Stream#limit(long)
     */
    public static <E> Iterator<E> limit(Iterator<E> iterator, long maxSize) {
        Objects.requireNonNull(iterator);
        if (maxSize < 0) {
            throw new IllegalArgumentException(maxSize + " < 0"); //$NON-NLS-1$
        }

        return new LimitIterator<>(iterator, maxSize);
    }

    /**
     * Returns an iterator that discards a number of elements at the start of another iterator.
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     *          Discarded elements will not be returned by the returned iterator, and therefore cannot be removed.
     * @param <E> The element type.
     * @param iterator The iterator for which to discard the first elements.
     * @param n The number of elements to discard.
     * @return An iterator that discards the first {@code n} elements of the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @throws IllegalArgumentException If the given number of elements is negative.
     * @see Stream#skip(long)
     */
    public static <E> Iterator<E> skip(Iterator<E> iterator, long n) {
        Objects.requireNonNull(iterator);
        if (n < 0) {
            throw new IllegalArgumentException(n + " < 0"); //$NON-NLS-1$
        }

        return new SkipIterator<>(iterator, n);
    }

    /**
     * Returns an iterator that discards elements of another iterator once an element matches a specific predicate.
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     *          Discarded elements will not be returned by the returned iterator, and therefore cannot be removed.
     * @param <E> The element type.
     * @param iterator The iterator for which to discard elements.
     * @param predicate The predicate that determines when elements are discarded.
     * @return An iterator that discards elements of the given iterator once an element matches the given predicate.
     * @throws NullPointerException If the given iterator or predicate is {@code null}.
     */
    public static <E> Iterator<E> takeWhile(Iterator<E> iterator, Predicate<? super E> predicate) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(predicate);

        return new TakeWhileIterator<>(iterator, predicate);
    }

    /**
     * Returns an iterator that discards elements of another iterator until an element does not match a specific predicate.
     *
     * @apiNote The returned iterator supports the {@link Iterator#remove()} operation if the given iterator does.
     *          Discarded elements will not be returned by the returned iterator, and therefore cannot be removed.
     * @param <E> The element type.
     * @param iterator The iterator for which to discard elements.
     * @param predicate The predicate that determines until when elements are discarded.
     * @return An iterator that discards elements of the given iterator until an element does not match the given predicate.
     * @throws NullPointerException If the given iterator or predicate is {@code null}.
     */
    public static <E> Iterator<E> dropWhile(Iterator<E> iterator, Predicate<? super E> predicate) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(predicate);

        return new DropWhileIterator<>(iterator, predicate);
    }

    /**
     * Performs a reduction on the elements of an iterator.
     *
     * @param <E> The element type.
     * @param iterator The iterator with the elements to perform a reduction on.
     * @param identity The identity value for the accumulating function.
     * @param accumulator A function for combining two values.
     * @return The result of the reduction.
     * @throws NullPointerException If the given iterator or accumulator function is {@code null}.
     * @see Stream#reduce(Object, BinaryOperator)
     */
    public static <E> E reduce(Iterator<E> iterator, E identity, BinaryOperator<E> accumulator) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(accumulator);

        E result = identity;
        while (iterator.hasNext()) {
            E element = iterator.next();
            result = accumulator.apply(result, element);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of an iterator.
     *
     * @param <E> The element type.
     * @param iterator The iterator with the elements to perform a reduction on.
     * @param accumulator A function for combining two values.
     * @return An {@link Optional} describing the result of the reduction, or {@link Optional#empty()} if the given iterator has no elements.
     * @throws NullPointerException If the given iterator or accumulator function or the result of the reduction is {@code null}.
     * @see Stream#reduce(BinaryOperator)
     */
    public static <E> Optional<E> reduce(Iterator<E> iterator, BinaryOperator<E> accumulator) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(accumulator);

        if (iterator.hasNext()) {
            E result = iterator.next();
            while (iterator.hasNext()) {
                E element = iterator.next();
                result = accumulator.apply(result, element);
            }
            return Optional.of(result);
        }
        return Optional.empty();
    }

    /**
     * Performs a reduction on the elements of an iterator.
     *
     * @param <E> The element type of the input iterator.
     * @param <U> The element type of the resulting iterator.
     * @param iterator The iterator with the elements to perform a reduction on.
     * @param identity The identity value for the accumulating function.
     * @param accumulator A function for combining two values.
     * @return The result of the reduction.
     * @throws NullPointerException If the given iterator or accumulator function is {@code null}.
     * @see Stream#reduce(Object, BiFunction, BinaryOperator)
     */
    public static <E, U> U reduce(Iterator<E> iterator, U identity, BiFunction<U, ? super E, U> accumulator) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(accumulator);

        U result = identity;
        while (iterator.hasNext()) {
            E element = iterator.next();
            result = accumulator.apply(result, element);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of an iterator.
     *
     * @param <E> The element type of the input iterator.
     * @param <R> The element type of the resulting iterator.
     * @param iterator The iterator with the elements to perform a reduction on.
     * @param supplier A supplier for mutable result containers.
     * @param accumulator A function that folds elements into a result container.
     * @return The result of the reduction.
     * @throws NullPointerException If the given iterator, supplier or accumulator function is {@code null}.
     * @see Stream#collect(Supplier, BiConsumer, BiConsumer)
     */
    public static <E, R> R collect(Iterator<E> iterator, Supplier<R> supplier, BiConsumer<R, ? super E> accumulator) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(accumulator);

        R result = supplier.get();
        while (iterator.hasNext()) {
            E element = iterator.next();
            accumulator.accept(result, element);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of an iterator.
     *
     * @param <E> The element type of the input iterator.
     * @param <R> The element type of the resulting iterator.
     * @param <A> The intermediate accumulation type of the collector.
     * @param iterator The iterator with the elements to perform a reduction on.
     * @param collector The collector to use for the reduction.
     * @return The result of the reduction.
     * @throws NullPointerException If the given iterator or collector is {@code null}.
     * @see Stream#collect(Collector)
     */
    public static <E, R, A> R collect(Iterator<E> iterator, Collector<? super E, A, R> collector) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(collector);

        A intermediate = collector.supplier().get();
        while (iterator.hasNext()) {
            E element = iterator.next();
            collector.accumulator().accept(intermediate, element);
        }
        return collector.finisher().apply(intermediate);
    }

    /**
     * Returns the minimum element of an iterator according to a specific comparator.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return the minimum element of.
     * @param comparator The comparator to use for comparing elements.
     * @return An {@link Optional} describing the minimum element, or {@link Optional#empty()} if the given iterator has no elements.
     * @throws NullPointerException If the given iterator or comparator is {@code null}.
     * @see Stream#min(Comparator)
     */
    public static <E> Optional<E> min(Iterator<E> iterator, Comparator<? super E> comparator) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(comparator);

        if (iterator.hasNext()) {
            E result = iterator.next();
            while (iterator.hasNext()) {
                E element = iterator.next();
                if (comparator.compare(element, result) < 0) {
                    result = element;
                }
            }
            return Optional.of(result);
        }
        return Optional.empty();
    }

    /**
     * Returns the maximum element of an iterator according to a specific comparator.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return the maximum element of.
     * @param comparator The comparator to use for comparing elements.
     * @return An {@link Optional} describing the maximum element, or {@link Optional#empty()} if the given iterator has no elements.
     * @throws NullPointerException If the given iterator or comparator is {@code null}.
     * @see Stream#max(Comparator)
     */
    public static <E> Optional<E> max(Iterator<E> iterator, Comparator<? super E> comparator) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(comparator);

        if (iterator.hasNext()) {
            E result = iterator.next();
            while (iterator.hasNext()) {
                E element = iterator.next();
                if (comparator.compare(element, result) > 0) {
                    result = element;
                }
            }
            return Optional.of(result);
        }
        return Optional.empty();
    }

    /**
     * Returns the number of elements of an iterator.
     *
     * @param iterator The iterator to return the number of elements of.
     * @return The number of elements of the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @see Stream#count()
     */
    public static long count(Iterator<?> iterator) {
        Objects.requireNonNull(iterator);

        long result = 0;
        while (iterator.hasNext()) {
            iterator.next();
            result++;
        }
        return result;
    }

    /**
     * Returns whether or not at least one element of an iterator matches a specific predicate.
     *
     * @param <E> The element type.
     * @param iterator The iterator with the elements to match.
     * @param predicate The predicate to apply to elements of the given iterator.
     * @return {@code true} if at least one element of the given iterator matches the given predicate, or {@code false} otherwise.
     * @throws NullPointerException If the given iterator or predicate is {@code null}.
     * @see Stream#anyMatch(Predicate)
     */
    public static <E> boolean anyMatch(Iterator<E> iterator, Predicate<? super E> predicate) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(predicate);

        while (iterator.hasNext()) {
            E element = iterator.next();
            if (predicate.test(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether or not all elements of an iterator match a specific predicate.
     *
     * @param <E> The element type.
     * @param iterator The iterator with the elements to match.
     * @param predicate The predicate to apply to elements of the given iterator.
     * @return {@code true} if all elements of the given iterator match the given predicate, or {@code false} otherwise.
     * @throws NullPointerException If the given iterator or predicate is {@code null}.
     * @see Stream#allMatch(Predicate)
     */
    public static <E> boolean allMatch(Iterator<E> iterator, Predicate<? super E> predicate) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(predicate);

        while (iterator.hasNext()) {
            E element = iterator.next();
            if (!predicate.test(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether or not no element of an iterator matches a specific predicate.
     *
     * @param <E> The element type.
     * @param iterator The iterator with the elements to match.
     * @param predicate The predicate to apply to elements of the given iterator.
     * @return {@code true} if no element of the given iterator matches the given predicate, or {@code false} otherwise.
     * @throws NullPointerException If the given iterator or predicate is {@code null}.
     * @see Stream#noneMatch(Predicate)
     */
    public static <E> boolean noneMatch(Iterator<E> iterator, Predicate<? super E> predicate) {
        Objects.requireNonNull(iterator);
        Objects.requireNonNull(predicate);

        while (iterator.hasNext()) {
            E element = iterator.next();
            if (predicate.test(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the first element of an iterator.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return the first element of.
     * @return An {@link Optional} describing the first element, or {@link Optional#empty()} if the given iterator has no elements.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @see Stream#findFirst()
     */
    public static <E> Optional<E> findFirst(Iterator<E> iterator) {
        Objects.requireNonNull(iterator);

        if (iterator.hasNext()) {
            E element = iterator.next();
            return Optional.of(element);
        }
        return Optional.empty();
    }

    /**
     * Returns a stream based on an iterator.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return a stream for.
     * @return A stream based on the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @see Spliterators#spliteratorUnknownSize(Iterator, int)
     * @see StreamSupport#stream(Spliterator, boolean)
     */
    public static <E> Stream<E> toStream(Iterator<E> iterator) {
        return toStream(iterator, 0);
    }

    /**
     * Returns a stream based on an iterator.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return a stream for.
     * @param characteristics Characteristics of the iterator's elements.
     * @return A stream based on the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @see Spliterators#spliteratorUnknownSize(Iterator, int)
     * @see StreamSupport#stream(Spliterator, boolean)
     */
    public static <E> Stream<E> toStream(Iterator<E> iterator, int characteristics) {
        Objects.requireNonNull(iterator);

        Spliterator<E> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);
        return StreamSupport.stream(spliterator, false);
    }

    /**
     * Returns a stream based on an iterator.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return a stream for.
     * @param size The number of elements in the iterator.
     * @return A stream based on the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @see Spliterators#spliterator(Iterator, long, int)
     * @see StreamSupport#stream(Spliterator, boolean)
     */
    public static <E> Stream<E> toStream(Iterator<E> iterator, long size) {
        return toStream(iterator, size, 0);
    }

    /**
     * Returns a stream based on an iterator.
     *
     * @param <E> The element type.
     * @param iterator The iterator to return a stream for.
     * @param size The number of elements in the iterator.
     * @param characteristics Characteristics of the iterator's elements.
     * @return A stream based on the given iterator.
     * @throws NullPointerException If the given iterator is {@code null}.
     * @see Spliterators#spliterator(Iterator, long, int)
     * @see StreamSupport#stream(Spliterator, boolean)
     */
    public static <E> Stream<E> toStream(Iterator<E> iterator, long size, int characteristics) {
        Objects.requireNonNull(iterator);

        Spliterator<E> spliterator = Spliterators.spliterator(iterator, size, characteristics);
        return StreamSupport.stream(spliterator, false);
    }

    /**
     * Returns an iterator that contains the elements of several other iterators.
     *
     * @param <E> The element type.
     * @param iterators The iterators with the elements for the returned iterator.
     * @return An iterator that contains the elements of the given iterators.
     * @throws NullPointerException If any of the given iterators is {@code null}.
     */
    @SafeVarargs
    public static <E> Iterator<E> chainIterators(Iterator<? extends E>... iterators) {
        return chainIterators(Arrays.asList(iterators));
    }

    /**
     * Returns an iterator that contains the elements of several other iterators.
     *
     * @param <E> The element type.
     * @param iterators The iterators with the elements for the returned iterator.
     * @return An iterator that contains the elements of the given iterators.
     * @throws NullPointerException If any of the given iterators is {@code null}.
     */
    public static <E> Iterator<E> chainIterators(Iterable<? extends Iterator<? extends E>> iterators) {
        return new FlatMapIterator<>(iterators.iterator(), Function.identity());
    }

    /**
     * Returns an iterator that contains the elements of several other iterables.
     *
     * @param <E> The element type.
     * @param iterables The iterables with the elements for the returned iterator.
     * @return An iterator that contains the elements of the given iterables.
     * @throws NullPointerException If any of the given iterables is {@code null}.
     */
    @SafeVarargs
    public static <E> Iterator<E> chainIterables(Iterable<? extends E>... iterables) {
        return chainIterables(Arrays.asList(iterables));
    }

    /**
     * Returns an iterator that contains the elements of several other iterables.
     *
     * @param <E> The element type.
     * @param iterables The iterables with the elements for the returned iterator.
     * @return An iterator that contains the elements of the given iterables.
     * @throws NullPointerException If any of the given iterables is {@code null}.
     */
    public static <E> Iterator<E> chainIterables(Iterable<? extends Iterable<? extends E>> iterables) {
        return new FlatMapIterator<>(iterables.iterator(), Iterable::iterator);
    }

    /**
     * Returns an iterator for which the elements are produced by iterative application of a function.
     *
     * @param <E> The element type.
     * @param seed The initial element.
     * @param hasNext A predicate that determines when iteration ends.
     * @param next A function that determines the next element based on the previous one.
     * @return An iterator for which the elements are produced by iterative application of the given function.
     */
    public static <E> Iterator<E> iterate(E seed, Predicate<? super E> hasNext, UnaryOperator<E> next) {
        Objects.requireNonNull(hasNext);
        Objects.requireNonNull(next);

        return new OperatorBasedIterator<>(seed, hasNext, next);
    }
}
