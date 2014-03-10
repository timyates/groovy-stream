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

import groovy.stream.functions.Function2 ;
import groovy.stream.iterators.groovy.ZipIterator ;

import java.util.Iterator ;

public class ZipIteratorForFunction<T,U,V> extends ZipIterator<T,U,V> {
    private final Function2<T,U,V> function ;

    public ZipIteratorForFunction( Iterator<T> iter1, Iterator<U> iter2, Function2<T,U,V> method ) {
        super( iter1, iter2, false, null ) ;
        this.function = method ;
    }

    @Override
    protected V performZip( T a, U b ) {
        return function.call( a, b ) ;
    }
}