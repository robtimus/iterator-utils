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
 * <p>
 * Sub classes should implement {@link #findNext(Consumer)} to find the next element. Furthermore, sub classes that should support the
 * {@link Iterator#remove()} operation should also override {@link #remove(Object)}.
 *
 * @author Rob Spoor
 * @apiNote Removing elements has not been implemented. It is up to sub classes to implement it if removing elements needs to be supported.
 * @param <E> The element type.
 */
public abstract class LookaheadIterator<E> implements Iterator<E> {

    private IterationState iterationState;
    private E next;

    // For removing
    private E removable;
    private boolean canRemove;

    /**
     * Creates a new lookahead iterator.
     */
    protected LookaheadIterator() {
        iterationState = IterationState.UNKNOWN;
        next = null;

        removable = null;
        canRemove = false;
    }

    /**
     * Tries to finds the next element. If the next element could be found, the provided consumer should be called with the next element. Otherwise,
     * the method can end without calling the consumer.
     *
     * @param next The consumer to call with the next element.
     */
    protected abstract void findNext(Consumer<E> next);

    @Override
    public final boolean hasNext() {
        if (iterationState == IterationState.UNKNOWN) {
            // Assume ended unless the consumer in findNext is triggered
            iterationState = IterationState.ENDED;
            findNext(element -> {
                next = element;
                iterationState = IterationState.HAS_NEXT;
                // Leave removable and canRemove intact; those should only be altered by next and remove
            });
        }
        return iterationState == IterationState.HAS_NEXT;
    }

    @Override
    public final E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        removable = next;
        canRemove = true;

        next = null;
        iterationState = IterationState.UNKNOWN;

        return removable;
    }

    /**
     * Removes the latest element returned by {@link Iterator#next()}.
     *
     * @implSpec The default implementation always throws an {@link UnsupportedOperationException}.
     * @param element The element to remove.
     */
    protected void remove(E element) {
        throw new UnsupportedOperationException("remove"); //$NON-NLS-1$
    }

    @Override
    public final void remove() {
        if (!canRemove) {
            throw new IllegalStateException();
        }
        remove(removable);

        removable = null;
        canRemove = false;
    }

    private enum IterationState {
        /*
         * Unknown whether or not iteration can continue or has ended
         * Initial state
         * Transition from HAS_NEXT through next
         * Transition to HAS_NEXT or ENDED using findNext
         */
        UNKNOWN,
        /*
         * Iteration can continue
         * Transition from UNKNOWN using findNext
         * Transition to UNKNOWN through next
         */
        HAS_NEXT,
        /*
         * Final state
         * Transition from UNKNOWN using findNext
         */
        ENDED,
    }
}
