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

import groovy.stream.functions.StreamPredicate ;
import groovy.stream.iterators.groovy.FilteringIterator ;

import java.util.Iterator ;

public class FilteringPredicateIterator<T> extends FilteringIterator<T> {
    private final StreamPredicate<T> predicateFn ;

    public FilteringPredicateIterator( Iterator<T> iterator, StreamPredicate<T> predicate ) {
        super( iterator, null, false ) ;
        this.predicateFn = predicate ;
    }

    @Override
    protected void loadNext() {
        while( iterator.hasNext() ) {
            current = iterator.next() ;
            boolean result = predicateFn.call( current ) ;
            if( result ) return ;
        }
        exhausted = true ;
    }
}