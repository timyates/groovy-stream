package groovy.stream.iterators.java ;

import groovy.stream.functions.* ;
import org.junit.* ;
import static org.junit.Assert.* ;
import java.util.* ;

public class FlatMapFnIteratorUnitTests {
    List<Integer> list = Arrays.asList( 1, 2, null, 4, 5 ) ;
    FlatMapIteratorForFunction<Integer,Integer> iter ;

    @Before
    public void setUp() {
        iter = new FlatMapIteratorForFunction<Integer,Integer>( list.iterator(), new Function<Integer,Collection<Integer>>() {
            @Override
            public Collection<Integer> call( Integer i ) {
                return Arrays.asList( i, i ) ;
            }
        } ) ;
    }

    @Test
    public void checkWeGetValuesBack() {
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( 1, 1, 2, 2, null, null, 4, 4, 5, 5 ) ) ;
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