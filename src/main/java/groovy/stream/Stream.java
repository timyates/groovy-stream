package groovy.stream ;

import groovy.lang.Closure ;

import groovy.stream.iterators.* ;

import java.lang.reflect.Array;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;

public class Stream<T> implements Iterator<T>, Iterable<T> {
    Iterator<T> iterator ;

    private Stream( Iterator<T> iterator ) {
        this.iterator = iterator ;
    }

    public Stream<T> filter( Closure<Boolean> predicate ) {
        return new Stream<T>( new FilteringIterator<T>( iterator, predicate ) ) ;
    }

    public Stream<T> flatMap( Closure<Collection<T>> map ) { 
        return new Stream<T>( new FlatMapIterator<T,Collection<T>>( iterator, map ) ) ;
    }

    public Stream<T> tap( Closure<Void> output ) { return tapEvery( 1, output ) ; }
    public Stream<T> tapEvery( int n, Closure<Void> output ) {
        return new Stream<T>( new TapIterator<T>( iterator, n, output ) ) ;
    }

    public <U> Stream<U> map( Closure<U> map ) {
        return new Stream<U>( new TransformingIterator<T,U>( iterator, map ) ) ;
    }

    public Stream<T> until( Closure<Boolean> predicate ) {
        return new Stream<T>( new UntilIterator<T>( iterator, predicate ) ) ;
    }

    public Stream<Collection<T>> collate( int size ) { return collate( size, size, true ) ; }
    public Stream<Collection<T>> collate( int size, boolean keepRemainder ) { return collate( size, size, keepRemainder ) ; }
    public Stream<Collection<T>> collate( int size, int step ) { return collate( size, step, true ) ; }
    public Stream<Collection<T>> collate( int size, int step, boolean keepRemainder ) {
        return new Stream<Collection<T>>( new CollatingIterator<T>( this.iterator, size, step, keepRemainder ) ) ;
    }

    public static <T> Stream<Map<Object,T>> from( Map<Object,Iterable<T>> map ) { return new Stream<Map<Object,T>>( new MapIterator<Object,T>( map ) ) ;     }
    public static <T> Stream<T>             from( Stream<T> stream            ) { return new Stream<T>( stream.iterator ) ;                                         }
    public static <T> Stream<T>             from( Iterable<T> iterable        ) { return new Stream<T>( iterable.iterator() ) ;                              }
    public static <T> Stream<T>             from( Iterator<T> iterator        ) { return new Stream<T>( iterator ) ;                                         }
    public static <T> Stream<T>             from( Closure<T> closure          ) { return new Stream<T>( new RepeatingClosureIterator<T>( closure ) ) ;       }
    @SuppressWarnings("unchecked")
    public static <T> Stream<T>             from( T[] array                   ) { return new Stream<T>(         primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Byte>          from( byte[] array                ) { return new Stream<Byte>(      primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Character>     from( char[] array                ) { return new Stream<Character>( primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Short>         from( short[] array               ) { return new Stream<Short>(     primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Integer>       from( int[] array                 ) { return new Stream<Integer>(   primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Long>          from( long[] array                ) { return new Stream<Long>(      primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Float>         from( float[] array               ) { return new Stream<Float>(     primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Double>        from( double[] array              ) { return new Stream<Double>(    primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Boolean>       from( boolean[] array             ) { return new Stream<Boolean>(   primitiveArrayToList( array ).iterator() ) ; }

    /* Iterator and Iterable Methods */
    @Override public Iterator<T> iterator() { return this ; }
    @Override public T next()               { return iterator.next() ; }
    @Override public boolean hasNext()      { return iterator.hasNext() ; }
    @Override public void remove()          { iterator.remove() ; }

    /* Utilities */
    @SuppressWarnings("unchecked")
    private static List primitiveArrayToList( Object array ) {
        int size = Array.getLength( array ) ;
        List list = new ArrayList( size ) ;
        for( int i = 0 ; i < size ; i++ ) {
            Object item = Array.get( array, i ) ;
            if( item != null && item.getClass().isArray() && item.getClass().getComponentType().isPrimitive() ) {
                item = primitiveArrayToList( item ) ;
            }
            list.add( item ) ;
        }
        return list ;
    }
}
