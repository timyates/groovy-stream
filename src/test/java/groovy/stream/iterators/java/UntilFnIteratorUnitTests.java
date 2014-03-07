package groovy.stream.iterators.java ;

import groovy.stream.functions.* ;
import org.junit.* ;
import static org.junit.Assert.* ;
import java.util.* ;

public class UntilFnIteratorUnitTests {
    List<Integer> list = Arrays.asList( 1, 2, null, 4, 5 ) ;
    UntilIteratorForPredicate<Integer> iter ;

    @Before
    public void setUp() {
        iter = new UntilIteratorForPredicate<Integer>( list.iterator(), new Predicate<Integer>() {
            @Override
            public boolean call( Integer i ) {
                return i == null ;
            }
        } ) ;
    }

    @Test
    public void checkWeGetValuesBack() {
        List<Integer> result = new ArrayList<Integer>() ;
        while( iter.hasNext() ) {
            result.add( iter.next() ) ;
        }
        assertEquals( result, Arrays.asList( 1, 2 ) ) ;
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