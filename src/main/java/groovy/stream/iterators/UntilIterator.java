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

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation ;

public class UntilIterator<T> implements Iterator<T> {
    private final Iterator<T>      iterator ;
    private final Closure<Boolean> predicate ;
    private final boolean          withIndex ;

    private T       current ;
    private int     index = 0 ;
    private boolean exhausted ;
    private boolean loaded ;

    public UntilIterator( Iterator<T> iterator, Closure<Boolean> predicate, boolean withIndex ) {
        this.iterator = iterator ;
        this.predicate = predicate ;
        this.withIndex = withIndex ;
        this.exhausted = false ;
        this.loaded = false ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    private void loadNext() {
        if( iterator.hasNext() ) {
            current = iterator.next() ;
            predicate.setDelegate( current ) ;
            Boolean check = withIndex ?
                                DefaultTypeTransformation.castToBoolean( predicate.call( current, index ) ) :
                                DefaultTypeTransformation.castToBoolean( predicate.call( current ) ) ;
            index++ ;
            if( check ) {
                exhausted = true ;
            }
        }
        else {
            exhausted = true ;
        }
    }

    @Override
    public boolean hasNext() {
        if( !loaded ) {
            loadNext() ;
            loaded = true ;
        }
        return !exhausted ;
    }

    @Override
    public T next() {
        hasNext() ;
        if( exhausted ) {
            throw new NoSuchElementException( "FilterIterator has been exhausted and contains no more elements" ) ;
        }
        T ret = current ;
        loaded = false ;
        return ret ;
    }
}