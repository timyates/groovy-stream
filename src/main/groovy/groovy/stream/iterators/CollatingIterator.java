/*
 * Copyright 2013 the original author or authors.
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

import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Queue ;

public class CollatingIterator<T> implements Iterator<List<T>> {
    private Iterator<T> parent ;
    private int size ;
    private int step ;
    private boolean keepRemainder ;

    private List<T> current ;
    private Queue<List<T>> cache = new LinkedList<List<T>>() ;
    private boolean initialised  = false ;
    private int index            = 0 ;

    public CollatingIterator( Iterator<T> parent, int size ) {
        this( parent, size, size, true ) ;
    }

    public CollatingIterator( Iterator<T> parent, int size, int step ) {
        this( parent, size, step, true ) ;
    }

    public CollatingIterator( Iterator<T> parent, int size, boolean keepRemainder ) {
        this( parent, size, size, keepRemainder ) ;
    }

    public CollatingIterator( Iterator<T> parent, int size, int step, boolean keepRemainder ) {
        if( size < 1 ) {
            throw new IllegalArgumentException( "Collation size must be > 1" ) ;
        }
        if( step < 1 ) {
            throw new IllegalArgumentException( "Step size must be > 1" ) ;
        }
        this.parent = parent ;
        this.size = size ;
        this.step = step ;
        this.keepRemainder = keepRemainder ;
    }

    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    private void loadNext() {
        while( parent.hasNext() ) {
            T next = parent.next() ;
            if( index % step == 0 ) {
                cache.offer( new ArrayList<T>() ) ;
            }
            index++ ;
            for( List<T> item : cache ) {
                item.add( next ) ;
            }
            if( cache.peek().size() == size ) {
                break ;
            }
        }
        current = cache.poll() ;
        if( !keepRemainder && current.size() < size ) {
            current = null ;
        }
    }

    public boolean hasNext() {
        if( !initialised ) {
            loadNext() ;
            initialised = true ;
        }
        return current != null ;
    }

    public List<T> next() {
        if( !initialised ) {
            hasNext() ;
        }
        List<T> ret = new ArrayList<T>( current ) ;
        loadNext() ;
        return ret ;
    }
}