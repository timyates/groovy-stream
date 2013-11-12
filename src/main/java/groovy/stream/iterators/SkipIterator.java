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

package groovy.stream ;

import groovy.lang.Closure ;
import java.util.Collection ;
import java.util.Iterator ;
import java.util.LinkedList ;
import java.util.Queue ;
import java.util.NoSuchElementException ;

public class SkipIterator<T> implements Iterator<T> {
    private Iterator<T>   parent ;
    private int           numberToSkip ;
    private T             current ;
    private boolean       initialised ;
    private boolean       exhausted ;

    public SkipIterator( Iterator<T> parent, int numberToSkip ) {
        this.parent = parent ;
        this.numberToSkip = numberToSkip ;
        this.initialised = false ;
        this.current = null ;
        this.exhausted = false ;
    }

    public void remove() {
        parent.remove() ;
    }

    private void loadNext() {
        while( numberToSkip-- > 0 && !exhausted ) {
            if( parent.hasNext() ) {
                current = parent.next() ;
            }
            else {
                exhausted = true ;
            }
        }
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
            throw new NoSuchElementException( "SkipIterator has been exhausted and contains no more elements" ) ;
        }
        T ret = current ;
        loadNext() ;
        return ret ;
    }
}