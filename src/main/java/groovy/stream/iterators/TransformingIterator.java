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

import groovy.lang.Closure ;
import java.util.Iterator ;
import java.util.NoSuchElementException ;

public class TransformingIterator<T,U> implements Iterator<U> {
    private final Iterator<T> iterator ;
    private final Closure<U>  mapping ;
    private final boolean     withIndex ;

    private U       current ;
    private int     index = 0 ;
    private boolean exhausted ;
    private boolean initialised ;

    public TransformingIterator( Iterator<T> iterator, Closure<U> mapping, boolean withIndex ) {
        this.iterator = iterator ;
        this.mapping = mapping ;
        this.withIndex = withIndex ;
        this.exhausted = false ;
        this.initialised = false ;
    }

    @Override
    public void remove() {
        iterator.remove() ;
    }

    private void loadNext() {
        if( iterator.hasNext() ) {
            T next = iterator.next() ;
            mapping.setDelegate( next ) ;
            current = withIndex ? mapping.call( next, index ) : mapping.call( next ) ;
            index++ ;
        }
        else {
            exhausted = true ;
        }
    }

    @Override
    public synchronized boolean hasNext() {
        if( !initialised ) {
            loadNext() ;
            initialised = true ;
        }
        return !exhausted ;
    }

    @Override
    public synchronized U next() {
        if( !initialised ) {
            hasNext() ;
        }
        if( exhausted ) {
            throw new NoSuchElementException( "TransformingIterator has been exhausted and contains no more elements" ) ;
        }
        U ret = current ;
        loadNext() ;
        return ret ;
    }
}