package groovy.stream;

import groovy.stream.functions.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class StreamUnitTests {
    Stream<Integer> stream;

    @Before
    public void setUp() {
        stream = Stream.from( Arrays.asList( 1, 2, 3, 4 ) );
    }

    private <T> List<T> collectIterator( Iterator<T> i ) {
        List<T> ret = new ArrayList<T>() ;
        while( i.hasNext() ) {
            ret.add( i.next() ) ;
        }
        return ret ;
    }

    @Test
    public void testFilteringStream() {
        Stream<Integer> s = stream.filter( new Predicate<Integer>() {
            @Override
            public boolean call( Integer value ) {
                return value % 2 == 1;
            }
        } );

        assertEquals( collectIterator( s ), Arrays.asList( 1, 3 ) );
    }

    @Test
    public void testIndexedFilteringStream() {
        Stream<Integer> s = stream.filterWithIndex( new IndexedPredicate<Integer>() {
            @Override
            public boolean call( Integer value, Integer index ) {
                return index % 2 == 1;
            }
        } );

        assertEquals( collectIterator( s ), Arrays.asList( 2, 4 ) );
    }

    @Test
    public void testTapStream() {
        final List<String> tapped = new ArrayList<String>() ;
        Stream<Integer> s = stream.tap( new Function<Integer, Void>() {
            @Override
            public Void call( Integer value ) {
                tapped.add( String.format( "Tap %d", value ) ) ;
                return null ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( 1, 2, 3, 4 ) );
        assertEquals( tapped, Arrays.asList( "Tap 1", "Tap 2", "Tap 3", "Tap 4" ) ) ;
    }

    @Test
    public void testTapWithIndexStream() {
        final List<String> tapped = new ArrayList<String>() ;
        Stream<Integer> s = stream.tapWithIndex( new IndexedFunction<Integer, Void>() {
            @Override
            public Void call( Integer value, Integer index ) {
                tapped.add( String.format( "Tap %d", value * index ) ) ;
                return null ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( 1, 2, 3, 4 ) );
        assertEquals( tapped, Arrays.asList( "Tap 0", "Tap 2", "Tap 6", "Tap 12" ) ) ;
    }

    @Test
    public void testMappedStream() {
        Stream<String> s = stream.map( new Function<Integer, String>() {
            @Override
            public String call( Integer value ) {
                return String.format( "Item%d", value ) ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( "Item1", "Item2", "Item3", "Item4" ) );
    }

    @Test
    public void testMappedIndexedStream() {
        Stream<String> s = stream.mapWithIndex( new IndexedFunction<Integer, String>() {
            @Override
            public String call( Integer value, Integer index ) {
                return String.format( "Item%d", value * index );
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( "Item0", "Item2", "Item6", "Item12" ) );
    }

    @Test
    public void testUntilStream() {
        Stream<Integer> s = stream.until( new Predicate<Integer>() {
            @Override
            public boolean call( Integer value ) {
                return value == 2 ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( 1 ) );
    }

    @Test
    public void testUntilIndexedStream() {
        Stream<Integer> s = stream.untilWithIndex( new IndexedPredicate<Integer>() {
            @Override
            public boolean call( Integer value, Integer index ) {
                return index * value > 6;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( 1, 2, 3 ) );
    }

    @Test
    public void testZipStream() {
        List<String> names = Arrays.asList( "One", "Two", "Three" ) ;
        Stream<String> s = stream.zip( names.iterator(), new Function2<Integer, String, String>() {
            @Override
            public String call( Integer iValue, String sValue ) {
                return String.format( "%s%d", sValue, iValue ) ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( "One1", "Two2", "Three3" ) );
    }

    @Test
    public void testZipStreamWithIterable() {
        List<String> names = Arrays.asList( "One", "Two", "Three" ) ;
        Stream<String> s = stream.zip( names, new Function2<Integer, String, String>() {
            @Override
            public String call( Integer iValue, String sValue ) {
                return String.format( "%s%d", sValue, iValue ) ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( "One1", "Two2", "Three3" ) );
    }

    @Test
    public void testZipIndexedStream() {
        List<String> names = Arrays.asList( "One", "Two", "Three" ) ;
        Stream<String> s = stream.zipWithIndex( names.iterator(), new IndexedFunction2<Integer, String, String>() {
            @Override
            public String call( Integer iValue, String sValue, Integer index ) {
                return String.format( "%s%d", sValue, iValue * index );
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( "One0", "Two2", "Three6" ) );
    }

    @Test
    public void testZipIndexedStreamWithIterable() {
        List<String> names = Arrays.asList( "One", "Two", "Three" ) ;
        Stream<String> s = stream.zipWithIndex( names, new IndexedFunction2<Integer, String, String>() {
            @Override
            public String call( Integer iValue, String sValue, Integer index ) {
                return String.format( "%s%d", sValue, iValue * index );
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( "One0", "Two2", "Three6" ) );
    }

    @Test
    public void testFlatMapStream() {
        Stream<Integer> s = stream.flatMap( new Function<Integer, List<Integer>>() {
            @Override
            public List<Integer> call( Integer value ) {
                return Arrays.asList( value, value ) ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( 1, 1, 2, 2, 3, 3, 4, 4 ) );
    }

    @Test
    public void testFlatMapIndexedStream() {
        Stream<Integer> s = stream.flatMapWithIndex( new IndexedFunction<Integer, List<Integer>>() {
            @Override
            public List<Integer> call( Integer value, Integer index ) {
                List<Integer> ret = new ArrayList<Integer>() ;
                for( int i = 0 ; i < index ; i++ ) {
                    ret.add( value ) ;
                }
                return ret ;
            }
        } ) ;

        assertEquals( collectIterator( s ), Arrays.asList( 2, 3, 3, 4, 4, 4 ) );
    }
 }
