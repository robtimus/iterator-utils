/*
 * OperatorBasedIteratorTest.java
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class OperatorBasedIteratorTest {

    @Test
    @DisplayName("iteration with remove")
    void testIterationWithRemove() {
        final int size = 10;

        Iterator<Integer> iterator = IteratorUtils.iterate(0, i -> i < size, i -> i + 1);

        assertThrows(UnsupportedOperationException.class, iterator::remove);

        assertTrue(iterator.hasNext());

        // hasNext has no side effects if the state is already known
        assertTrue(iterator.hasNext());

        for (int i = 0; i < size; i++) {
            assertTrue(iterator.hasNext(), "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

            assertEquals(i, iterator.next(), "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
        }

        assertFalse(iterator.hasNext());

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("iteration with remove without hasNext")
    void testIterationWithRemoveWithoutHasNext() {
        final int size = 10;

        Iterator<Integer> iterator = IteratorUtils.iterate(0, i -> i < size, i -> i + 1);

        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (int i = 0; i < size; i++) {
            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

            assertEquals(i, iterator.next(), "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
        }

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("forEachRemaining")
    void testForEachRemaining() {
        final int size = 10;

        Iterator<Integer> iterator = IteratorUtils.iterate(0, i -> i < size, i -> i + 1);

        List<Integer> values = new ArrayList<>();

        iterator.forEachRemaining(values::add);

        assertEquals(IntStream.range(0, size).boxed().collect(Collectors.toList()), values);
    }
}
