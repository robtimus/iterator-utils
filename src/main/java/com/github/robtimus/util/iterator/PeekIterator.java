/*
 * PeekIterator.java
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
import java.util.Objects;
import java.util.function.Consumer;

final class PeekIterator<E> implements Iterator<E> {

    private final Iterator<E> delegate;
    private final Consumer<? super E> action;

    PeekIterator(Iterator<E> delegate, Consumer<? super E> action) {
        this.delegate = delegate;
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        E element = delegate.next();
        action.accept(element);
        return element;
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        delegate.forEachRemaining(e -> {
            this.action.accept(e);
            action.accept(e);
        });
    }
}
