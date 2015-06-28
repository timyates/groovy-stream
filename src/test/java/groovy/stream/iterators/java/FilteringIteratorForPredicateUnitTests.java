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

import groovy.stream.functions.* ;
import groovy.stream.iterators.DelegatingCloseableIterator;
import org.junit.* ;
import static org.junit.Assert.* ;
import java.util.* ;

public class FilteringIteratorForPredicateUnitTests {
    List<Integer> list = Arrays.asList( 1, 2, null, 4, 5 ) ;
    FilteringIteratorForPredicate<Integer> iter ;

    @Before
    public void setUp() {
        iter = new FilteringIteratorForPredicate<Integer>( new DelegatingCloseableIterator<Integer>(list.iterator()), new Predicate<Integer>() {
            @Override
            public boolean call( Integer i ) {
                return i != null && i % 2 == 1;
            }
        } ) ;
    }

    @Test
    public void checkWeGetValuesBack() {
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( 1, 5 ) ) ;
    }

    @Test
    public void checkWeCanCallNextWithNoHasNext() {
        assertEquals( (Integer)1, iter.next() ) ;
        assertEquals( true, iter.hasNext() ) ;
    }

    @Test( expected=UnsupportedOperationException.class )
    public void checkUnsupportedException() {
        iter.remove() ;
    }

    @Test( expected=NoSuchElementException.class )
    public void checkNoSuchElementException() {
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        iter.next() ;
    }
}