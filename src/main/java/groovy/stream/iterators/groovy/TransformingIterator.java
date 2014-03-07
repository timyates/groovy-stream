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

public class TransformingIterator<T,U> extends AbstractIterator<U> {
    protected final Iterator<T> inputIterator ;
    private   final Closure<U>  mapping ;
    private   final boolean     withIndex ;
    protected int               index = 0 ;

    public TransformingIterator( Iterator<T> iterator, Closure<U> mapping, boolean withIndex ) {
        super( null ) ;
        this.inputIterator = iterator ;
        this.mapping = mapping ;
        this.withIndex = withIndex ;
    }

    @Override
    protected void loadNext() {
        if( inputIterator.hasNext() ) {
            T next = inputIterator.next() ;
            mapping.setDelegate( next ) ;
            current = withIndex ? mapping.call( next, index ) : mapping.call( next ) ;
            index++ ;
        }
        else {
            exhausted = true ;
        }
    }
}