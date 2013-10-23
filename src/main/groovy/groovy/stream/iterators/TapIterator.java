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

public class TapIterator<T> implements Iterator<T> {
    private Iterator<T> parent ;
    private int every ;
    private int index ;
    private Closure<Void> output ;
    private T current ;
    private boolean initialised ;

    public TapIterator( Iterator<T> parent, int every, Closure<Void> output ) {
        this.parent = parent ;
        this.every = every ;
        this.index = 0 ;
        this.output = output ;
        this.initialised = false ;
        this.current = null ;
    }

    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    private void loadNext() {
        if( parent.hasNext() ) {
            current = parent.next() ;

        }
        else {
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

    public T next() {
        if( !initialised ) {
            hasNext() ;
        }
        T ret = current ;
        if( ++index % every == 0 ) {
            if( output.getMaximumNumberOfParameters() == 1 ) {
                output.call( index ) ;
            }
            else {
                output.call( index, ret ) ;
            }
        }
        loadNext() ;
        return ret ;
    }
}