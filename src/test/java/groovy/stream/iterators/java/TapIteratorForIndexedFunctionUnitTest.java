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

import groovy.stream.functions.IndexedFunction;
import groovy.stream.iterators.DelegatingCloseableIterator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;

public class TapIteratorForIndexedFunctionUnitTest {
    List<Integer> list = Arrays.asList( 1, 2, null, 4, 5 ) ;
    List<String>  tapped ;
    TapIteratorForIndexedFunction<Integer> iter ;

    @Before
    public void setUp() {
        tapped = new ArrayList<String>() ;
        iter = new TapIteratorForIndexedFunction<Integer>( new DelegatingCloseableIterator<Integer>(list.iterator()), 2, new IndexedFunction<Integer,Void>() {
            @Override
            public Void call( Integer value, Integer index ) {
                tapped.add( String.format( "Tap %d @ %d", value, index ) ) ;
                return null ;
            }
        } ) ;
    }

    @Test
    public void checkWithNoEveryParameter() {
        TapIteratorForIndexedFunction<Integer> iter = new TapIteratorForIndexedFunction<Integer>( new DelegatingCloseableIterator<Integer>(list.iterator()), new IndexedFunction<Integer,Void>() {
            @Override
            public Void call( Integer value, Integer index ) {
                tapped.add( String.format( "Tap %d @ %d", value, index ) ) ;
                return null ;
            }
        } ) ;
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( 1, 2, null, 4, 5 ) ) ;
        assertEquals( tapped, Arrays.asList( "Tap 1 @ 0", "Tap 2 @ 1", "Tap null @ 2", "Tap 4 @ 3", "Tap 5 @ 4" ) ) ;
    }

    @Test
    public void checkWeGetValuesBack() {
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( 1, 2, null, 4, 5 ) ) ;
        assertEquals( tapped, Arrays.asList( "Tap 2 @ 1", "Tap 4 @ 3" ) ) ;
    }

    @Test
    public void checkWeCanCallNextWithNoHasNext() {
        assertEquals( (Integer)1, iter.next() ) ;
        assertEquals( tapped, new ArrayList<String>() ) ;
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
