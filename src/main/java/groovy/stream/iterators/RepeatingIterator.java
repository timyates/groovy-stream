/*
 * Copyright 2013-2014 the original author or authors.
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

package groovy.stream.iterators;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class RepeatingIterator<T> implements Iterator<T> {
    private static final Integer FOREVER = -65536;
    private final Queue<T> queue = new LinkedList<T>();
    private boolean repeating;
    private Integer nRepeats;
    private Iterator<T> current;

    public RepeatingIterator(Iterator<T> parent) {
        this(parent, FOREVER);
    }

    public RepeatingIterator(Iterator<T> parent, Integer nRepeats) {
        if(nRepeats < 0 && !nRepeats.equals(FOREVER)) {
            throw new IllegalArgumentException("Cannot repeat a negative number of times");
        }
        this.nRepeats = nRepeats;
        this.current = nRepeats == 0 ? new EmptyIterator<T>() : parent;
        this.repeating = false;
    }

    @Override
    public boolean hasNext() {
        return nRepeats == FOREVER || nRepeats > 1 || current.hasNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    @Override
    public T next() {
        if (!current.hasNext()) {
            if (nRepeats == FOREVER || nRepeats-- > 1) {
                this.repeating = true;
                current = queue.iterator();
            }
            else {
                throw new NoSuchElementException("RepeatingIterator has been exhausted and contains no more elements");
            }
        }
        T ret = current.next();
        if (!repeating) {
            queue.offer(ret);
        }
        return ret;
    }
}