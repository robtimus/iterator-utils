/*
 * LookaheadIteratorTest.java
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class LookaheadIteratorTest {

    @Nested
    @DisplayName("with remove support")
    class WithRemoveSupport {

        @Test
        @DisplayName("iteration with remove")
        void testIterationWithRemove() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = new TestIteratorWithRemove(list);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            assertTrue(iterator.hasNext());

            // hasNext has no side effects if the state is already known
            assertTrue(iterator.hasNext());

            for (int i = 0; i < size; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                // cannot remove after hasNext without matching next
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                // can remove directly after next
                assertDoesNotThrow(iterator::remove, "index=" + i);

                // cannot remove directly after remove
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(originalList.subList(i + 1, size), list, "index=" + i);
            }

            assertFalse(iterator.hasNext());

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(Collections.emptyList(), list);
        }

        @Test
        @DisplayName("iteration with remove without hasNext")
        void testIterationWithRemoveWithoutHasNext() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = new TestIteratorWithRemove(list);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            for (int i = 0; i < size; i++) {
                // cannot remove before next
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                // can remove directly after next
                assertDoesNotThrow(iterator::remove, "index=" + i);

                // cannot remove directly after remove
                assertThrows(IllegalStateException.class, iterator::remove, "index=" + i);

                assertEquals(originalList.subList(i + 1, size), list, "index=" + i);
            }

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(Collections.emptyList(), list);
        }
    }

    @Nested
    @DisplayName("without remove support")
    class WithoutRemoveSupport {

        @Test
        @DisplayName("iteration with remove")
        void testIterationWithRemove() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = new TestIteratorWithoutRemove(list);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            assertTrue(iterator.hasNext());

            // hasNext has no side effects if the state is already known
            assertTrue(iterator.hasNext());

            // cannot remove after hasNext without matching next
            Class<? extends RuntimeException> expectedRemoveException = IllegalStateException.class;

            for (int i = 0; i < size; i++) {
                assertTrue(iterator.hasNext(), "index=" + i);

                // first iteration: cannot remove after hasNext without matching next
                // next iterations: remove still allowed but not supported directly after next
                assertThrows(expectedRemoveException, iterator::remove, "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                // remove allowed but not supported directly after next
                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

                // remove still allowed but not supported directly after next
                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

                assertEquals(originalList, list, "index=" + i);

                expectedRemoveException = UnsupportedOperationException.class;
            }

            assertFalse(iterator.hasNext());

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(originalList, list);
        }

        @Test
        @DisplayName("iteration with remove without hasNext")
        void testIterationWithRemoveWithoutHasNext() {
            final int size = 10;

            List<Integer> list = IntStream.range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
            List<Integer> originalList = new ArrayList<>(list);

            Iterator<Integer> iterator = new TestIteratorWithoutRemove(list);

            // cannot remove when initialized
            assertThrows(IllegalStateException.class, iterator::remove);

            // cannot remove after hasNext without matching next
            Class<? extends RuntimeException> expectedRemoveException = IllegalStateException.class;

            for (int i = 0; i < size; i++) {
                // first iteration: cannot remove after hasNext without matching next
                // next iterations: remove still allowed but not supported directly after next
                assertThrows(expectedRemoveException, iterator::remove, "index=" + i);

                assertEquals(i, iterator.next(), "index=" + i);

                // remove allowed but not supported directly after next
                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

                // remove still allowed but not supported directly after next
                assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

                assertEquals(originalList, list, "index=" + i);

                expectedRemoveException = UnsupportedOperationException.class;
            }

            assertThrows(NoSuchElementException.class, iterator::next);

            assertEquals(originalList, list);
        }
    }

    private static final class TestIteratorWithRemove extends LookaheadIterator<Integer> {

        private final Iterator<Integer> iterator;

        private TestIteratorWithRemove(List<Integer> list) {
            iterator = list.iterator();
        }

        @Override
        protected void findNext(Consumer<Integer> next) {
            if (iterator.hasNext()) {
                Integer element = iterator.next();
                next.accept(element);
            }
        }

        @Override
        protected void remove(Integer element) {
            iterator.remove();
        }
    }

    private static final class TestIteratorWithoutRemove extends LookaheadIterator<Integer> {

        private final Iterator<Integer> iterator;

        private TestIteratorWithoutRemove(List<Integer> list) {
            iterator = list.iterator();
        }

        @Override
        protected void findNext(Consumer<Integer> next) {
            if (iterator.hasNext()) {
                Integer element = iterator.next();
                next.accept(element);
            }
        }
    }
}
