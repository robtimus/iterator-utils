/*
 * IteratorUtilsTest.java
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
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class IteratorUtilsTest {

    // Other wrapping / creating methods are tested in tests for the iterator implementations

    @Nested
    @DisplayName("toStream")
    class ToStream {

        @Test
        @DisplayName("without size or characteristics")
        void testWithoutSizeOrCharacteristics() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            Stream<Integer> stream = IteratorUtils.toStream(list.iterator());

            assertEquals(list.stream().reduce(Integer::sum), stream.reduce(Integer::sum));
        }

        @Test
        @DisplayName("without size with characteristics")
        void testWithoutSizeWithCharacteristics() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            Stream<Integer> stream = IteratorUtils.toStream(list.iterator(), Spliterator.NONNULL | Spliterator.DISTINCT | Spliterator.SORTED);

            assertEquals(list.stream().reduce(Integer::sum), stream.reduce(Integer::sum));
        }

        @Test
        @DisplayName("with size without characteristics")
        void testWithSizeWithoutCharacteristics() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            Stream<Integer> stream = IteratorUtils.toStream(list.iterator(), (long) size);

            assertEquals(list.stream().reduce(Integer::sum), stream.reduce(Integer::sum));
        }

        @Test
        @DisplayName("with size and characteristics")
        void testWithSizeAndCharacteristics() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            Stream<Integer> stream = IteratorUtils.toStream(list.iterator(), size,
                    Spliterator.NONNULL | Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.SIZED);

            assertEquals(list.stream().reduce(Integer::sum), stream.reduce(Integer::sum));
        }
    }

    @Nested
    @DisplayName("chainIterators")
    class ChainIterators {

        @Test
        @DisplayName("no iterators")
        void testNoIterators() {
            Iterator<Integer> iterator = IteratorUtils.chainIterators();

            assertFalse(iterator.hasNext());
        }

        @Test
        @DisplayName("single iterator")
        void testSingleIterator() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = IteratorUtils.chainIterators(list.iterator());

            for (int i = 0; i < size; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
            }

            assertEquals(originalList, list);
        }

        @Test
        @DisplayName("multiple iterators")
        void testMultipleIterators() {
            final int size = 10;

            List<Integer> list1 = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> list2 = IntStream.range(size, size * 2)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> list3 = IntStream.range(size * 2, size * 3)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>();
            originalList.addAll(list1);
            originalList.addAll(list2);
            originalList.addAll(list3);

            Iterator<Integer> iterator = IteratorUtils.chainIterators(list1.iterator(), list2.iterator(), list3.iterator());

            for (int i = 0; i < size * 3; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
            }

            assertEquals(originalList.subList(0, size), list1);
            assertEquals(originalList.subList(size, size * 2), list2);
            assertEquals(originalList.subList(size * 2, size * 3), list3);
        }
    }

    @Nested
    @DisplayName("chainIterables")
    class ChainIterables {

        @Test
        @DisplayName("no iterables")
        void testNoIterators() {
            Iterator<Integer> iterator = IteratorUtils.chainIterables();

            assertFalse(iterator.hasNext());
        }

        @Test
        @DisplayName("single iterable")
        void testSingleIterator() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = IteratorUtils.chainIterables(list);

            for (int i = 0; i < size; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
            }

            assertEquals(originalList, list);
        }

        @Test
        @DisplayName("multiple iterables")
        void testMultipleIterators() {
            final int size = 10;

            List<Integer> list1 = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> list2 = IntStream.range(size, size * 2)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> list3 = IntStream.range(size * 2, size * 3)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>();
            originalList.addAll(list1);
            originalList.addAll(list2);
            originalList.addAll(list3);

            Iterator<Integer> iterator = IteratorUtils.chainIterables(list1, list2, list3);

            for (int i = 0; i < size * 3; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
            }

            assertEquals(originalList.subList(0, size), list1);
            assertEquals(originalList.subList(size, size * 2), list2);
            assertEquals(originalList.subList(size * 2, size * 3), list3);
        }
    }
}
