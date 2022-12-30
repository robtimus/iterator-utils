/*
 * FilterIteratorTest.java
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class FilterIteratorTest {

    @Nested
    @DisplayName("filter even")
    class FilterEven {

        @Test
        @DisplayName("iteration with remove")
        void testIterationWithRemove() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .filter(i -> (i & 1) == 0);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            assertTrue(iterator.hasNext());

            // hasNext has no side effects if the state is already known
            assertTrue(iterator.hasNext());

            for (int i = 0; i < size / 2; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                // cannot remove after hasNext without matching next
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(i * 2, iterator.next(), "index=" + i);

                // can remove directly after next
                assertDoesNotThrow(iterator::remove, "index=" + i);

                // cannot remove directly after remove
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(originalList.subList(0, i * 2 + 1).stream().filter(e -> (e & 1) != 0).collect(Collectors.toList()),
                        list.subList(0, i));
                assertEquals(originalList.subList(i * 2 + 2, size), list.subList(i + 1, list.size()));
            }

            assertFalse(iterator.hasNext());

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(list.stream().filter(i -> (i & 1) != 0).collect(Collectors.toList()), list);
        }

        @Test
        @DisplayName("iteration with remove without hasNext")
        void testIterationWithRemoveWithoutHasNext() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .filter(i -> (i & 1) == 0);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            for (int i = 0; i < size / 2; i++) {
                // cannot remove before next
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(i * 2, iterator.next(), "index=" + i);

                // can remove directly after next
                assertDoesNotThrow(iterator::remove, "index=" + i);

                // cannot remove directly after remove
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(originalList.subList(0, i * 2 + 1).stream().filter(e -> (e & 1) != 0).collect(Collectors.toList()),
                        list.subList(0, i));
                assertEquals(originalList.subList(i * 2 + 2, size), list.subList(i + 1, list.size()));
            }

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(list.stream().filter(i -> (i & 1) != 0).collect(Collectors.toList()), list);
        }

        @Test
        @DisplayName("forEachRemaining")
        void testForEachRemaining() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            Iterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .filter(i -> (i & 1) == 0);

            List<Integer> values = new ArrayList<>();

            iterator.forEachRemaining(values::add);

            assertEquals(list.stream().filter(i -> (i & 1) == 0).collect(Collectors.toList()), values);
        }
    }

    @Nested
    @DisplayName("filter odd")
    class FilterOdd {

        @Test
        @DisplayName("iteration with remove")
        void testIterationWithRemove() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .filter(i -> (i & 1) != 0);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            assertTrue(iterator.hasNext());

            // hasNext has no side effects if the state is already known
            assertTrue(iterator.hasNext());

            for (int i = 0; i < size / 2; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                // cannot remove after hasNext without matching next
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(i * 2 + 1, iterator.next(), "index=" + i);

                // can remove directly after next
                assertDoesNotThrow(iterator::remove, "index=" + i);

                // cannot remove directly after remove
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(originalList.subList(0, i * 2).stream().filter(e -> (e & 1) == 0).collect(Collectors.toList()),
                        list.subList(0, i));
                assertEquals(originalList.subList(i * 2 + 2, size), list.subList(i + 1, list.size()));
            }

            assertFalse(iterator.hasNext());

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(list.stream().filter(i -> (i & 1) == 0).collect(Collectors.toList()), list);
        }

        @Test
        @DisplayName("iteration with remove without hasNext")
        void testIterationWithRemoveWithoutHasNext() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .filter(i -> (i & 1) != 0);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            for (int i = 0; i < size / 2; i++) {
                // cannot remove before next
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(i * 2 + 1, iterator.next(), "index=" + i);

                // can remove directly after next
                assertDoesNotThrow(iterator::remove, "index=" + i);

                // cannot remove directly after remove
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(originalList.subList(0, i * 2).stream().filter(e -> (e & 1) == 0).collect(Collectors.toList()),
                        list.subList(0, i));
                assertEquals(originalList.subList(i * 2 + 2, size), list.subList(i + 1, list.size()));
            }

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(list.stream().filter(i -> (i & 1) == 0).collect(Collectors.toList()), list);
        }

        @Test
        @DisplayName("forEachRemaining")
        void testForEachRemaining() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());

            Iterator<Integer> iterator = StreamLikeIterator.backedBy(list.iterator())
                    .filter(i -> (i & 1) != 0);

            List<Integer> values = new ArrayList<>();

            iterator.forEachRemaining(values::add);

            assertEquals(list.stream().filter(i -> (i & 1) != 0).collect(Collectors.toList()), values);
        }
    }
}
