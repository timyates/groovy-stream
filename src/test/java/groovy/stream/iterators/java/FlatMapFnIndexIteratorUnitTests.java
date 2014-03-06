package groovy.stream.iterators.java ;

import groovy.stream.functions.* ;
import org.junit.* ;
import static org.junit.Assert.* ;
import java.util.* ;

public class FlatMapFnIndexIteratorUnitTests {
    List<Integer> list = Arrays.asList( 1, 2, null, 4, 5 ) ;
    FlatMapFnIndexIterator<Integer,Integer> iter ;

    @Before
    public void setUp() {
        iter = new FlatMapFnIndexIterator<Integer,Integer>( list.iterator(), new StreamWithIndexFunction<Integer,Collection<Integer>>() {
            @Override
            public Collection<Integer> call( Integer i, Integer index ) {
                List<Integer> result = new ArrayList<Integer>() ;
                for( int a = 0 ; a < index ; a++ ) {
                    result.add( i ) ;
                }
                return result ;
            }
        } ) ;
    }

    @Test
    public void checkWeGetValuesBack() {
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( 2, null, null, 4, 4, 4, 5, 5, 5, 5 ) ) ;
    }

    @Test
    public void checkWeCanCallNextWithNoHasNext() {
        assertEquals( (Integer)2, iter.next() ) ;
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