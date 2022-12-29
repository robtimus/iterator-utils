/*
 * LookaheadIterator.java
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
import java.util.function.Consumer;

/**
 * A base class for {@link Iterator} implementations that need to calculate the next value in order to let {@link Iterator#hasNext()} return whether
 * or not there is a next value.
 *
 * @author Rob Spoor
 * @apiNote Removing elements has not been implemented. It is up to sub classes to implement it if removing elements needs to be supported.
 * @param <E> The element type.
 */
public abstract class LookaheadIterator<E> implements Iterator<E> {

    private State state = State.UNSPECIFIED;
    private E next = null;

    /**
     * Tries to finds the next element. If the next element could be found, the provided consumer should be called with the next element. Otherwise,
     * the method can end without calling the consumer.
     *
     * @param next The consumer to call with the next element.
     */
    protected abstract void findNext(Consumer<E> next);

    @Override
    public boolean hasNext() {
        if (state == State.UNSPECIFIED) {
            // assume ended unless the consumer in findNext is triggered
            state = State.ENDED;
            findNext(element -> {
                next = element;
                state = State.ACTIVE;
            });
        }
        return state == State.ACTIVE;
    }

    @Override
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        E result = next;
        next = null;
        state = State.UNSPECIFIED;
        return result;
    }

    private enum State {
        ACTIVE,
        ENDED,
        UNSPECIFIED,
    }
}
