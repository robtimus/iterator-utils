/*
 * FlatMapIterator.java
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

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

final class FlatMapIterator<E, R> implements Iterator<R> {

    private final Iterator<E> delegate;
    private final Function<? super E, ? extends Iterator<? extends R>> mapper;

    private Iterator<? extends R> current;

    FlatMapIterator(Iterator<E> delegate, Function<? super E, ? extends Iterator<? extends R>> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
        this.current = Collections.emptyIterator();
    }

    @Override
    public boolean hasNext() {
        if (current.hasNext()) {
            return true;
        }
        while (delegate.hasNext()) {
            E element = delegate.next();
            current = mapper.apply(element);
            if (current != null && current.hasNext()) {
                return true;
            }
        }
        // each remaining element of delegate is mapped to a null or empty iterator
        return false;
    }

    @Override
    public R next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return current.next();
    }

    // don't implement remove

    @Override
    public void forEachRemaining(Consumer<? super R> action) {
        Objects.requireNonNull(action);
        current.forEachRemaining(action);
        delegate.forEachRemaining(e -> {
            current = mapper.apply(e);
            if (current != null) {
                current.forEachRemaining(action);
            }
        });
    }
}
