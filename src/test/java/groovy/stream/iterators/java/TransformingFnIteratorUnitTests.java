package groovy.stream.iterators.java ;

import groovy.stream.functions.* ;
import org.junit.* ;
import static org.junit.Assert.* ;
import java.util.* ;

public class TransformingFnIteratorUnitTests {
    List<Integer> list = Arrays.asList( 1, 2, null, 4, 5 ) ;
    TransformingIteratorForFunction<Integer,Integer> iter ;

    @Before
    public void setUp() {
        iter = new TransformingIteratorForFunction<Integer,Integer>( list.iterator(), new Function<Integer,Integer>() {
            @Override
            public Integer call( Integer i ) {
                return i == null ? null : i + 10 ;
            }
        } ) ;
    }

    @Test
    public void checkWeGetValuesBack() {
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( 11, 12, null, 14, 15 ) ) ;
    }

    @Test
    public void checkWeCanCallNextWithNoHasNext() {
        assertEquals( (Integer)11, iter.next() ) ;
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