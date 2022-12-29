/*
 * OperatorBasedIterator.java
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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

final class OperatorBasedIterator<E> implements Iterator<E> {

    private final Predicate<? super E> predicate;
    private final UnaryOperator<E> next;

    private E current;

    OperatorBasedIterator(E seed, Predicate<? super E> predicate, UnaryOperator<E> next) {
        this.predicate = predicate;
        this.next = next;
        this.current = seed;
    }

    @Override
    public boolean hasNext() {
        return predicate.test(current);
    }

    @Override
    public E next() {
        if (predicate.test(current)) {
            E result = current;
            current = next.apply(current);
            return result;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        while (predicate.test(current)) {
            action.accept(current);
            current = next.apply(current);
        }
    }
}
