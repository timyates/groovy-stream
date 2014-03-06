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

import groovy.stream.functions.StreamWithIndexPredicate ;
import groovy.stream.iterators.groovy.UntilIterator ;
import java.util.Collection ;
import java.util.Iterator ;

public class UntilFnIndexIterator<T> extends UntilIterator<T> {
    private final StreamWithIndexPredicate<T> predicateFn ;

    public UntilFnIndexIterator( Iterator<T> iterator, StreamWithIndexPredicate<T> predicateFn ) {
        super( iterator, null, true ) ;
        this.predicateFn = predicateFn ;
    }

    @Override
    protected void loadNext() {
        if( iterator.hasNext() ) {
            current = iterator.next() ;
            boolean check = predicateFn.call( current, index ) ;
            index++ ;
            if( check ) {
                exhausted = true ;
            }
        }
        else {
            exhausted = true ;
        }
    }
}