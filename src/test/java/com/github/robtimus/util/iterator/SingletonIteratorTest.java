/*
 * SingletonIteratorTest.java
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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class SingletonIteratorTest {

    @Test
    @DisplayName("iteration with remove")
    void testIterationWithRemove() {
        Iterator<Integer> iterator = IteratorUtils.singletonIterator(13);

        assertThrows(UnsupportedOperationException.class, iterator::remove);

        assertTrue(iterator.hasNext());

        // hasNext has no side effects if the state is already known
        assertTrue(iterator.hasNext());

        for (int i = 0; i < 1; i++) {
            assertTrue(iterator.hasNext(), "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

            assertEquals(13, iterator.next(), "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
        }

        assertFalse(iterator.hasNext());

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("iteration with remove without hasNext")
    void testIterationWithRemoveWithoutHasNext() {
        Iterator<Integer> iterator = IteratorUtils.singletonIterator(13);

        assertThrows(UnsupportedOperationException.class, iterator::remove);

        for (int i = 0; i < 1; i++) {
            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

            assertEquals(13, iterator.next(), "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);

            assertThrows(UnsupportedOperationException.class, iterator::remove, "index=" + i);
        }

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("forEachRemaining")
    void testForEachRemaining() {
        Iterator<Integer> iterator = IteratorUtils.singletonIterator(13);

        List<Integer> values = new ArrayList<>();

        iterator.forEachRemaining(values::add);

        assertEquals(Collections.singletonList(13), values);
    }
}
