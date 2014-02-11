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
import java.util.Collection ;
import java.util.Iterator ;
import java.util.LinkedList ;
import java.util.Queue ;
import java.util.NoSuchElementException ;

public class FlatMapIterator<T,U> implements Iterator<U> {
    private final Queue<U>    pushback = new LinkedList<U>() ;
    private final Iterator<T> iterator ;
    private final boolean     withIndex ;

    Closure<? extends Collection<U>> mapping ;
    private int            index = 0 ;
    private boolean        exhausted ;
    private boolean        initialised ;
    private U              current ;

    public FlatMapIterator( Iterator<T> iterator, Closure<? extends Collection<U>> mapping, boolean withIndex ) {
        this.mapping = mapping ;
        this.iterator = iterator ;
        this.withIndex = withIndex ;
        this.exhausted = false ;
        this.initialised = false ;
    }

    @Override
    public void remove() {
        iterator.remove() ;
    }

    private void loadNext() {
        while( pushback.isEmpty() && iterator.hasNext() ) {
            T next = iterator.next() ;
            mapping.setDelegate( next ) ;
            Collection<U> mapped = withIndex ? mapping.call( next, index ) : mapping.call( next ) ;
            index++ ;
            for( U element : mapped ) {
                pushback.offer( element ) ;
            }
        }
        if( !pushback.isEmpty() ) {
            current = pushback.poll() ;
        }
        else {
            exhausted = true ;
        }
    }

    @Override
    public boolean hasNext() {
        if( !initialised ) {
            loadNext() ;
            initialised = true ;
        }
        return !exhausted ;
    }

    @Override
    public U next() {
        if( !initialised ) {
            hasNext() ;
        }
        if( exhausted ) {
            throw new NoSuchElementException( "FlatMapIterator has been exhausted and contains no more elements" ) ;
        }
        U ret = current ;
        loadNext() ;
        return ret ;
    }
}