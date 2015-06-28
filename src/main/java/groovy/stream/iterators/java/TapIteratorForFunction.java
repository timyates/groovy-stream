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

package groovy.stream.iterators.java;

import groovy.stream.functions.Function ;
import groovy.stream.iterators.CloseableIterator;
import groovy.stream.iterators.groovy.TapIterator;

import java.util.Iterator;

public class TapIteratorForFunction<T> extends TapIterator<T> {
    private final Function<T,Void> function ;

    public TapIteratorForFunction( CloseableIterator<T> iterator, Function<T,Void> function ) {
        this( iterator, 1, function ) ;
    }

    public TapIteratorForFunction( CloseableIterator<T> iterator, int every, Function<T,Void> function ) {
        super( iterator, every, false, null ) ;
        this.function = function ;
    }

    @Override
    protected void performTap( T ret ) {
        if( ++index % every == 0 ) {
            function.call( ret ) ;
        }
    }
}
