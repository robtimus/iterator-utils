/*
 * LimitIterator.java
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

import java.util.Iterator;
import java.util.NoSuchElementException;

final class LimitIterator<E> implements Iterator<E> {

    private final Iterator<E> delegate;

    private long remaining;

    LimitIterator(Iterator<E> delegate, long maxSize) {
        this.delegate = delegate;
        this.remaining = maxSize;
    }

    @Override
    public boolean hasNext() {
        return remaining > 0 && delegate.hasNext();
    }

    @Override
    public E next() {
        if (remaining <= 0) {
            throw new NoSuchElementException();
        }
        E element = delegate.next();
        remaining--;
        return element;
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    // don't implement forEachRemaining, as it doesn't have a short-circuit
}
