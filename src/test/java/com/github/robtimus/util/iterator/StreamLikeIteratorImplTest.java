/*
 * StreamLikeIteratorImplTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class StreamLikeIteratorImplTest {

    // Intermediate steps separately are tested in tests for the iterator implementations

    @Nested
    @DisplayName("limit(long)")
    class Limit {

        @Test
        @DisplayName("negative maxSize")
        void testNegativeMaxSize() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertThrows(IllegalArgumentException.class, () -> iterator.limit(-1));
        }

        @Test
        @DisplayName("zero maxSize")
        void testZeroMaxSize() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .limit(0);

            assertFalse(iterator.hasNext());
        }

        @Test
        @DisplayName("MAX_VALUE maxSize")
        void testMaxValueMaxSize() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .limit(Long.MAX_VALUE);

            assertEquals(list.stream().reduce(Integer::sum), iterator.reduce(Integer::sum));
        }
    }

    @Nested
    @DisplayName("skip(long)")
    class Skip {

        @Test
        @DisplayName("negative n")
        void testNegativeMaxSize() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertThrows(IllegalArgumentException.class, () -> iterator.skip(-1));
        }

        @Test
        @DisplayName("zero n")
        void testZeroMaxSize() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .skip(0);

            assertEquals(list.stream().reduce(Integer::sum), iterator.reduce(Integer::sum));
        }

        @Test
        @DisplayName("MAX_VALUE maxSize")
        void testMaxValueMaxSize() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .skip(Long.MAX_VALUE);

            assertFalse(iterator.hasNext());
        }
    }

    @Nested
    @DisplayName("reduce(E, UnaryOperator)")
    class ReduceWithIdentity {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            BinaryOperator<Integer> accumulator = (i, j) -> i * j;
            assertEquals(1, iterator.reduce(1, accumulator));
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            BinaryOperator<Integer> accumulator = (i, j) -> i * j;
            assertEquals(list.stream().reduce(1, accumulator), iterator.reduce(1, accumulator));
        }
    }

    @Nested
    @DisplayName("reduce(UnaryOperator)")
    class ReduceWithoutIdentity {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            BinaryOperator<Integer> accumulator = (i, j) -> i * j;
            assertEquals(Optional.empty(), iterator.reduce(accumulator));
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            BinaryOperator<Integer> accumulator = (i, j) -> i * j;
            assertEquals(list.stream().reduce(accumulator), iterator.reduce(accumulator));
        }
    }

    @Nested
    @DisplayName("reduce(U, BiFunction)")
    class ReduceWithMapping {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            BiFunction<Long, Integer, Long> accumulator = (i, j) -> i * j;
            assertEquals(1L, iterator.reduce(1L, accumulator));
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            BiFunction<Long, Integer, Long> accumulator = (i, j) -> i * j;
            assertEquals(list.stream().reduce(1L, accumulator, Long::sum), iterator.reduce(1L, accumulator));
        }
    }

    @Nested
    @DisplayName("collect(Supplier, BiConsumer)")
    class CollectWithSupplierAndBiConsumer {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            Supplier<List<Integer>> supplier = ArrayList::new;
            BiConsumer<List<Integer>, Integer> accumulator = List::add;
            assertEquals(Collections.emptyList(), iterator.collect(supplier, accumulator));
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            Supplier<List<Integer>> supplier = ArrayList::new;
            BiConsumer<List<Integer>, Integer> accumulator = List::add;
            BiConsumer<List<Integer>, List<Integer>> combiner = List::addAll;
            assertEquals(list.stream().collect(supplier, accumulator, combiner), iterator.collect(supplier, accumulator));
        }
    }

    @Nested
    @DisplayName("collect(Collector)")
    class CollectWithCollector {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<String> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertEquals("", iterator.collect(Collectors.joining()));
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<String> list = new Random().ints(size)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.toList());

            StreamLikeIterator<String> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertEquals(list.stream().collect(Collectors.joining()), iterator.collect(Collectors.joining()));
        }
    }

    @Nested
    @DisplayName("min(Comparator)")
    class Min {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertEquals(Optional.empty(), iterator.min(Comparator.naturalOrder()));
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertEquals(list.stream().min(Comparator.naturalOrder()), iterator.min(Comparator.naturalOrder()));
        }
    }

    @Nested
    @DisplayName("max(Comparator)")
    class Max {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertEquals(Optional.empty(), iterator.max(Comparator.naturalOrder()));
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertEquals(list.stream().max(Comparator.naturalOrder()), iterator.max(Comparator.naturalOrder()));
        }
    }

    @Nested
    @DisplayName("count()")
    class Count {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertEquals(0, iterator.count());
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertEquals(size, iterator.count());
        }
    }

    @Nested
    @DisplayName("anyMatch(Predicate)")
    class AnyMatch {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertFalse(iterator.anyMatch(i -> true));
        }

        @Test
        @DisplayName("non-empty iterator - no matches")
        void testNonEmptyIteratorWithNoMatches() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertFalse(iterator.anyMatch(i -> false));
        }

        @Test
        @DisplayName("non-empty iterator - some matches")
        void testNonEmptyIteratorWithSomeMatches() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertTrue(iterator.anyMatch(i -> (i & 1) == 0));
        }

        @Test
        @DisplayName("non-empty iterator - all matches")
        void testNonEmptyIteratorWithAllMatches() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertTrue(iterator.anyMatch(i -> true));
        }
    }

    @Nested
    @DisplayName("allMatch(Predicate)")
    class AllMatch {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertTrue(iterator.allMatch(i -> true));
        }

        @Test
        @DisplayName("non-empty iterator - no matches")
        void testNonEmptyIteratorWithNoMatches() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertFalse(iterator.allMatch(i -> false));
        }

        @Test
        @DisplayName("non-empty iterator - some matches")
        void testNonEmptyIteratorWithSomeMatches() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertFalse(iterator.allMatch(i -> (i & 1) == 0));
        }

        @Test
        @DisplayName("non-empty iterator - all matches")
        void testNonEmptyIteratorWithAllMatches() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertTrue(iterator.allMatch(i -> true));
        }
    }

    @Nested
    @DisplayName("noneMatch(Predicate)")
    class NoneMatch {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertTrue(iterator.noneMatch(i -> true));
        }

        @Test
        @DisplayName("non-empty iterator - no matches")
        void testNonEmptyIteratorWithNoMatches() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertTrue(iterator.noneMatch(i -> false));
        }

        @Test
        @DisplayName("non-empty iterator - some matches")
        void testNonEmptyIteratorWithSomeMatches() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertFalse(iterator.noneMatch(i -> (i & 1) == 0));
        }

        @Test
        @DisplayName("non-empty iterator - all matches")
        void testNonEmptyIteratorWithAllMatches() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertFalse(iterator.noneMatch(i -> true));
        }
    }

    @Nested
    @DisplayName("findFirst()")
    class FindFirst {

        @Test
        @DisplayName("empty iterator")
        void testEmptyIterator() {
            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(Collections.emptyIterator());

            assertEquals(Optional.empty(), iterator.findFirst());
        }

        @Test
        @DisplayName("non-empty iterator")
        void testNonEmptyIterator() {
            final int size = 10;

            List<Integer> list = new Random().ints(size)
                    .boxed()
                    .collect(Collectors.toList());

            StreamLikeIterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator());

            assertEquals(Optional.of(list.get(0)), iterator.findFirst());
        }
    }
}
