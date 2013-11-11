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

import groovy.lang.Closure ;

import java.util.Iterator ;
import java.util.NoSuchElementException ;

public class TapIterator<T> implements Iterator<T> {
    private Iterator<T>   parent ;
    private int           every ;
    private int           index ;
    private boolean       withIndex ;
    private Closure<Void> output ;
    private T             current ;
    private boolean       initialised ;
    private boolean       exhausted ;

    public TapIterator( Iterator<T> parent, int every, boolean withIndex, Closure<Void> output ) {
        this.parent = parent ;
        this.every = every ;
        this.index = 0 ;
        this.withIndex = withIndex ;
        this.output = output ;
        this.initialised = false ;
        this.current = null ;
        this.exhausted = false ;
    }

    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    private void loadNext() {
        if( parent.hasNext() ) {
            current = parent.next() ;
        }
        else {
            exhausted = true ;
        }
    }

    public boolean hasNext() {
        if( !initialised ) {
            loadNext() ;
            initialised = true ;
        }
        return !exhausted ;
    }

    public T next() {
        if( !initialised ) {
            hasNext() ;
        }
        if( exhausted ) {
            throw new NoSuchElementException( "TapIterator has been exhausted and contains no more elements" ) ;
        }
        T ret = current ;
        if( ++index % every == 0 ) {
            if( withIndex ) {
                output.call( ret, index - 1 ) ; 
            }
            else {
                output.call( ret ) ;
            }
        }
        loadNext() ;
        return ret ;
    }
}