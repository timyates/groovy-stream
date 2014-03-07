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
import java.util.Iterator ;
import java.util.NoSuchElementException ;

public class ZipIterator<T,U,V> extends AbstractIterator<V> {
    protected final Iterator<T> iter1 ;
    protected final Iterator<U> iter2 ;
    private   final Closure<V>  method ;
    private   final boolean     withIndex ;
    protected int   index ;

    public ZipIterator( Iterator<T> iter1, Iterator<U> iter2, boolean withIndex, Closure<V> method ) {
        super( null ) ;
        this.iter1 = iter1 ;
        this.iter2 = iter2 ;
        this.method = method ;
        this.withIndex = withIndex ;
        this.loaded = false ;
        this.exhausted = false ;
        this.index = 0 ;
    }

    @Override
    protected void loadNext() {
        if( iter1.hasNext() ) {
            T obj1 = iter1.next() ;
            if( iter2.hasNext() ) {
                U obj2 = iter2.next() ;
                current = performZip( obj1, obj2 );
                index++ ;
            }
            else {
                exhausted = true ;
            }
        }
        else {
            exhausted = true ;
        }
    }

    protected V performZip( T obj1, U obj2 ) {
        return withIndex ? method.call( obj1, obj2, index ) : method.call( obj1, obj2 ) ;
    }
}