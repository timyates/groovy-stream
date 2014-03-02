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

package groovy.stream.iterators ;

import java.util.Iterator ;
import java.util.NoSuchElementException ;

public class ConcatenationIterator<T> implements Iterator<T> {
    private final Iterator<? extends T> first ;
    private final Iterator<? extends T> last ;

    private T       current ;
    private boolean loaded    = false ;
    private boolean exhausted = false ;

    public ConcatenationIterator( Iterator<? extends T> first,
                                  Iterator<? extends T> last ) {
        this.first = first ;
        this.last = last ;
    }

    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    private void loadNext() {
        if( !first.hasNext() && !last.hasNext() ) {
            exhausted = true ;
        }
        else if( first.hasNext() ) {
            current = first.next() ;
        }
        else {
            current = last.next() ;
        }
    }

    public boolean hasNext() {
        if( !loaded   ) {
            loadNext() ;
            loaded   = true ;
        }
        return !exhausted ;
    }

    public T next() {
        if( !loaded   ) {
            hasNext() ;
        }
        if( exhausted ) {
            throw new NoSuchElementException( "ConcatenationIterator has been exhausted and contains no more elements" ) ;
        }
        T ret = current ;
        loaded = false ;
        return ret ;
    }
}