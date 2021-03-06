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
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation ;

public class FilteringIterator<T> extends AbstractIterator<T> {
    private   final Closure<Boolean> predicate ;
    private   final boolean withIndex ;
    protected int     index = 0 ;

    public FilteringIterator( Iterator<T> iterator, Closure<Boolean> predicate, boolean withIndex ) {
        super( iterator ) ;
        this.predicate = predicate ;
        this.withIndex = withIndex ;
    }

    @Override
    protected void loadNext() {
        while( iterator.hasNext() ) {
            current = iterator.next() ;
            setDelegate();
            Boolean result = callFilter();
            index++ ;                                 
            if( result ) return ;
        }
        exhausted = true ;
    }

    protected boolean callFilter() {
        return withIndex ? DefaultTypeTransformation.castToBoolean( predicate.call( current, index ) ) :
                           DefaultTypeTransformation.castToBoolean( predicate.call( current ) );
    }

    protected void setDelegate() {
        predicate.setDelegate( current );
    }
}