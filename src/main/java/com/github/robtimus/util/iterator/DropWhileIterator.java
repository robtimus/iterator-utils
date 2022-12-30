/*
 * DropWhileIterator.java
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
import java.util.function.Predicate;

final class DropWhileIterator<E> extends LookaheadIterator<E> {

    private final Iterator<E> delegate;
    private final Predicate<? super E> predicate;

    private boolean nonMatchFound;

    DropWhileIterator(Iterator<E> delegate, Predicate<? super E> predicate) {
        this.delegate = delegate;
        this.predicate = predicate;
        this.nonMatchFound = false;
    }

    @Override
    protected void findNext(Consumer<E> next) {
        while (!nonMatchFound && delegate.hasNext()) {
            E element = delegate.next();
            if (!predicate.test(element)) {
                nonMatchFound = true;
                next.accept(element);
                return;
            }
        }
        if (delegate.hasNext()) {
            // a non-match was found
            E element = delegate.next();
            next.accept(element);
        }
        // else all remaining elements were dropped, don't call next
    }

    @Override
    protected void remove(E element) {
        delegate.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        delegate.forEachRemaining(e -> {
            if (nonMatchFound) {
                action.accept(e);
            } else if (!predicate.test(e)) {
                nonMatchFound = true;
                action.accept(e);
            }
            // else no non-match has been found yet, drop e
        });
    }
}
