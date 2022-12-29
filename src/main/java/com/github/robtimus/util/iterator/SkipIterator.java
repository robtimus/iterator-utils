/*
 * SkipIterator.java
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

final class SkipIterator<E> extends LookaheadIterator<E> {

    private final Iterator<E> delegate;

    private long remaining;

    SkipIterator(Iterator<E> delegate, long n) {
        this.delegate = delegate;
        this.remaining = n;
    }

    @Override
    protected void findNext(Consumer<E> next) {
        while (remaining > 0 && delegate.hasNext()) {
            // discard next
            delegate.next();
            remaining--;
        }
        if (delegate.hasNext()) {
            // remaining == 0
            E element = delegate.next();
            next.accept(element);
        }
        // else all remaining elements were skipped, don't call next
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        delegate.forEachRemaining(e -> {
            if (remaining > 0) {
                remaining--;
            } else {
                action.accept(e);
            }
        });
    }
}
