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
import org.junit.* ;
import static org.junit.Assert.* ;
import java.util.* ;

public class ZipIteratorForFunctionUnitTests {
    List<Integer> listA = Arrays.asList( 1, 2, null, 4, 5 ) ;
    List<String>  listB = Arrays.asList( "A", null, "B", "C", "D" ) ;
    ZipIteratorForFunction<Integer,String,String> iter ;

    @Before
    public void setUp() {
        iter = new ZipIteratorForFunction<Integer,String,String>( listA.iterator(),
                                                                  listB.iterator(),
                                                                  new Function2<Integer,String,String>() {
            @Override
            public String call( Integer a, String b ) {
                return String.format( "%d%s", a, b ) ;
            }
        } ) ;
    }

    @Test
    public void checkWeGetValuesBack() {
        List<String> result = new ArrayList<String>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( "1A", "2null", "nullB", "4C", "5D" ) ) ;
    }

    @Test
    public void checkWeCanCallNextWithNoHasNext() {
        assertEquals( "1A", iter.next() ) ;
        assertEquals( true, iter.hasNext() ) ;
    }

    @Test( expected=UnsupportedOperationException.class )
    public void checkUnsupportedException() {
        iter.remove() ;
    }

    @Test( expected=NoSuchElementException.class )
    public void checkNoSuchElementException() {
        List<String> result = new ArrayList<String>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        iter.next() ;
    }
}