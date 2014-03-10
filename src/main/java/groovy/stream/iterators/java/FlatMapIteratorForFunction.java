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

import groovy.stream.functions.Function ;
import groovy.stream.iterators.groovy.FlatMapIterator ;
import java.util.Collection ;
import java.util.Iterator ;

public class FlatMapIteratorForFunction<T,U> extends FlatMapIterator<T,U> {
    private final Function<T,? extends Collection<U>> mappingFn ;

    public FlatMapIteratorForFunction( Iterator<T> iterator, Function<T, ? extends Collection<U>> mapping ) {
        super( iterator, null, false ) ;
        this.mappingFn = mapping ;
    }

    @Override
    protected void setDelegate( T next ) {
        // Cannot set delegate
    }

    @Override
    protected Collection<U> performMapping( T next ) {
        return mappingFn.call( next ) ;
    }
}