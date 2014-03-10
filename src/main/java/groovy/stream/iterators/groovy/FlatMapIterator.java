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

package groovy.stream.iterators.groovy ;

import groovy.lang.Closure ;
import groovy.stream.iterators.AbstractIterator ;
import java.util.Collection ;
import java.util.Iterator ;
import java.util.LinkedList ;
import java.util.Queue ;

public class FlatMapIterator<T,U> extends AbstractIterator<U> {
    protected final Queue<U>    pushback = new LinkedList<U>() ;
    protected final Iterator<T> inputIterator ;
    private   final boolean     withIndex ;
    protected int            index = 0 ;
    private final Closure<? extends Collection<U>> mapping ;

    public FlatMapIterator( Iterator<T> iterator, Closure<? extends Collection<U>> mapping, boolean withIndex ) {
        super( null ) ;
        this.mapping = mapping ;
        this.inputIterator = iterator ;
        this.withIndex = withIndex ;
    }

    @Override
    protected void loadNext() {
        while( pushback.isEmpty() && inputIterator.hasNext() ) {
            T next = inputIterator.next() ;
            setDelegate( next );
            Collection<U> mapped = performMapping( next ) ;
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

    protected Collection<U> performMapping( T next ) {
        return withIndex ? mapping.call( next, index ) : mapping.call( next );
    }

    protected void setDelegate( T next ) {
        mapping.setDelegate( next ) ;
    }
}