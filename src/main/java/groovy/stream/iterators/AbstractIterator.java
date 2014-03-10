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

public abstract class AbstractIterator<T> implements Iterator<T> {
    protected final Iterator<T>   iterator ;

    protected T       current ;
    protected boolean loaded ;
    protected boolean exhausted ;

    public AbstractIterator( Iterator<T> parentIterator ) {
        this.iterator = parentIterator ;
        this.loaded = false ;
        this.current = null ;
        this.exhausted = false ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    protected abstract void loadNext() ;

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
            throw new NoSuchElementException( "Iterator has been exhausted and contains no more elements" ) ;
        }
        T ret = current ;
        loaded = false ;
        return ret ;
    }
}