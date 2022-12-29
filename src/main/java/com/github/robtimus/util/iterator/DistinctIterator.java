/*
 * DistinctIterator.java
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

final class DistinctIterator<E> extends LookaheadIterator<E> {

    private final Iterator<E> delegate;
    private final Set<E> seen;

    DistinctIterator(Iterator<E> delegate) {
        this.delegate = delegate;
        this.seen = new HashSet<>();
    }

    @Override
    protected void findNext(Consumer<E> next) {
        while (delegate.hasNext()) {
            E element = delegate.next();
            if (seen.add(element)) {
                next.accept(element);
                return;
            }
        }
        // all remaining elements were already seen, don't call next
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        delegate.forEachRemaining(e -> {
            if (seen.add(e)) {
                action.accept(e);
            }
        });
    }
}
