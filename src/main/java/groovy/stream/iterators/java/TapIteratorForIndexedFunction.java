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

package groovy.stream.iterators.java ;

import groovy.stream.functions.IndexedFunction ;
import groovy.stream.iterators.groovy.TapIterator;

import java.util.Iterator;

public class TapIteratorForIndexedFunction<T> extends TapIterator<T> {
    private final IndexedFunction<T,Void> function ;

    public TapIteratorForIndexedFunction( Iterator<T> iterator, IndexedFunction<T,Void> function ) {
        this( iterator, 1, function ) ;
    }

    public TapIteratorForIndexedFunction( Iterator<T> iterator, int every, IndexedFunction<T,Void> function ) {
        super( iterator, every, true, null ) ;
        this.function = function ;
    }

    @Override
    protected void performTap( T ret ) {
        if( ++index % every == 0 ) {
            function.call( ret, index - 1 );
        }
    }
}
