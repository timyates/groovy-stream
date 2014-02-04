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

package groovy.stream ;

import groovy.lang.Closure ;

import groovy.stream.iterators.* ;

import java.io.BufferedReader ;

import java.lang.reflect.Array;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;

import org.codehaus.groovy.runtime.DefaultGroovyMethods ;

/**
 *
 * @author Tim Yates
 * @param <T>
 */
public class Stream<T> implements Iterator<T>, Iterable<T> {
    private final Iterator<T> iterator ;

    private Stream( Iterator<T> iterator ) {
        this.iterator = iterator ;
    }

    /**
     * Filter the current stream, passing each element through a predicate filter.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..3 )
     *                .filter { it % 2 == 1 }
     *                .collect() == [ 1, 3 ]
     * </pre>
     *
     * @param predicate A single parameter closure to pass the element through,
     *                   returning {@code true} if the element is to be included.
     * @return A new {@code Stream} wrapping a {@link FilteringIterator}
     */
    public Stream<T> filter( Closure<Boolean> predicate ) {
        return new Stream<T>( new FilteringIterator<T>( iterator, predicate, false ) ) ;
    }

    /**
     * Filter the current stream, passing each element and it's index through a predicate filter.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..3 )
     *                .filterWithIndex { it, index -> index % 2 == 1 }
     *                .collect() == [ 2 ]
     * </pre>
     *
     * @param predicate A two parameter closure, the first parameter being the
     *                   element in the {@code Stream}, the second the index (starting at 0).
     * @return A new {@code Stream} wrapping a {@link FilteringIterator}
     */
    public Stream<T> filterWithIndex( Closure<Boolean> predicate ) {
        return new Stream<T>( new FilteringIterator<T>( iterator, predicate, true ) ) ;
    }

    /**
     * Returns a new {@code Stream} which will iterate the elements in the current {@code Stream},
     * followed by the elements in the {@code other} {@code Stream}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def a = Stream.from( 1..3 )
     *   def b = Stream.from( 'a'..'c' )
     *
     *   assert a.concat( b ).collect() == [ 1, 2, 3, 'a', 'b', 'c' ]
     * </pre>
     *
     * @param other The {@code Stream} to iterate after the current one is exhausted.
     * @return A new {@code Stream} wrapping a {@link ConcatenationIterator}
     */
    public Stream<T> concat( Iterator<? extends T> other ) {
        return new Stream<T>( new ConcatenationIterator<T>( iterator, other ) ) ;
    }

    /**
     * Skip {@code n} elements.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..10 )
     *                .skip( 6 ).collect() == [ 7, 8, 9, 10 ]
     * </pre>
     *
     * @param n the number of elements to skip
     * @return A new {@code Stream} wrapping a {@link SkipIterator}
     */
    public Stream<T> skip( int n ) {
        return new Stream<T>( new SkipIterator<T>( iterator, n ) ) ;
    }

    /**
     * Takes a {@code Closure} that returns a {@code Collection}.  Each element
     * in this {@code Collection} is passed on in turn, before the next element is
     * fetched from upstream, and the Closure executed again.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..3 )
     *                .flatMap { [ it ] * it }
     *                .collect() == [ 1, 2, 2, 3, 3, 3 ]
     * </pre>
     *
     * @param map A single parameter closure to pass the element through,
     *            returning a new Collection of elements to iterate.
     * @return A new {@code Stream} wrapping a {@link FlatMapIterator}
     */
    public Stream<T> flatMap( Closure<Collection<T>> map ) { 
        return new Stream<T>( new FlatMapIterator<T,Collection<T>>( iterator, map, false ) ) ;
    }

    /**
     *
     * @param map
     * @return A new {@code Stream} wrapping a {@link FlatMapIterator}
     */
    public Stream<T> flatMapWithIndex( Closure<Collection<T>> map ) { 
        return new Stream<T>( new FlatMapIterator<T,Collection<T>>( iterator, map, true ) ) ;
    }

    /**
     *
     * @param output
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tap( Closure<Void> output ) { return tapEvery( 1, output ) ; }

    /**
     *
     * @param n
     * @param output
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tapEvery( int n, Closure<Void> output ) {
        return new Stream<T>( new TapIterator<T>( iterator, n, false, output ) ) ;
    }

    /**
     *
     * @param output
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tapWithIndex( Closure<Void> output ) { return tapEveryWithIndex( 1, output ) ; }

    /**
     *
     * @param n
     * @param output
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tapEveryWithIndex( int n, Closure<Void> output ) {
        return new Stream<T>( new TapIterator<T>( iterator, n, true, output ) ) ;
    }

    /**
     *
     * @param <U>
     * @param map
     * @return A new {@code Stream} wrapping a {@link TransformingIterator}
     */
    public <U> Stream<U> map( Closure<U> map ) {
        return new Stream<U>( new TransformingIterator<T,U>( iterator, map, false ) ) ;
    }

    /**
     *
     * @param <U>
     * @param map
     * @return A new {@code Stream} wrapping a {@link TransformingIterator}
     */
    public <U> Stream<U> mapWithIndex( Closure<U> map ) {
        return new Stream<U>( new TransformingIterator<T,U>( iterator, map, true ) ) ;
    }

    /**
     *
     * @param predicate
     * @return A new {@code Stream} wrapping an {@link UntilIterator}
     */
    public Stream<T> until( Closure<Boolean> predicate ) {
        return new Stream<T>( new UntilIterator<T>( iterator, predicate, false ) ) ;
    }

    /**
     *
     * @param predicate
     * @return A new {@code Stream} wrapping an {@link UntilIterator}
     */
    public Stream<T> untilWithIndex( Closure<Boolean> predicate ) {
        return new Stream<T>( new UntilIterator<T>( iterator, predicate, true ) ) ;
    }

    /**
     *
     * @param size
     * @return
     */
    public Stream<Collection<T>> collate( int size ) { return collate( size, size, true ) ; }

    /**
     *
     * @param size
     * @param keepRemainder
     * @return
     */
    public Stream<Collection<T>> collate( int size, boolean keepRemainder ) { return collate( size, size, keepRemainder ) ; }

    /**
     *
     * @param size
     * @param step
     * @return
     */
    public Stream<Collection<T>> collate( int size, int step ) { return collate( size, step, true ) ; }

    /**
     *
     * @param size
     * @param step
     * @param keepRemainder
     * @return
     */
    public Stream<Collection<T>> collate( int size, int step, boolean keepRemainder ) {
        return new Stream<Collection<T>>( new CollatingIterator<T>( this.iterator, size, step, keepRemainder ) ) ;
    }

    /**
     *
     * @param <U>
     * @param <V>
     * @param other
     * @param map
     * @return
     */
    public <U,V> Stream<V> zip( Iterator<U> other, Closure<V> map ) {
        return new Stream<V>( new ZipIterator<T,U,V>( this.iterator, other, false, map ) ) ;
    }

    /**
     *
     * @param <U>
     * @param <V>
     * @param other
     * @param map
     * @return
     */
    public <U,V> Stream<V> zipWithIndex( Iterator<U> other, Closure<V> map ) {
        return new Stream<V>( new ZipIterator<T,U,V>( this.iterator, other, true, map ) ) ;
    }

    /**
     *
     * @param n
     * @return
     */
    public Iterator<T> take( int n ) {
        return DefaultGroovyMethods.take( (Iterator<T>)this, n ) ;
    }

    /**
     *
     * @param <K>
     * @param <V>
     * @param map
     * @return
     */
    public static <K,V> Stream<Map<K,V>>  from( Map<K,? extends Iterable<V>> map ) { return new Stream<Map<K,V>>( new MapIterator<K,V>( map ) ) ;               }

    /**
     *
     * @param <T>
     * @param stream
     * @return
     */
    public static <T>   Stream<T>         from( Stream<T> stream                 ) { return new Stream<T>( stream.iterator ) ;                                  }

    /**
     *
     * @param <T>
     * @param iterable
     * @return
     */
    public static <T>   Stream<T>         from( Iterable<T> iterable             ) { return new Stream<T>( iterable.iterator() ) ;                              }

    /**
     *
     * @param <T>
     * @param iterator
     * @return
     */
    public static <T>   Stream<T>         from( Iterator<T> iterator             ) { return new Stream<T>( iterator ) ;                                         }

    /**
     *
     * @param <T>
     * @param iterator
     * @return
     */
    public static       Stream<String>    from( BufferedReader reader            ) { return new Stream<String>( new BufferedReaderIterator( reader ) ) ;        }

    /**
     *
     * @param <T>
     * @param closure
     * @return
     */
    public static <T>   Stream<T>         from( Closure<T> closure               ) { return new Stream<T>( new RepeatingClosureIterator<T>( closure ) ) ;       }

    /**
     *
     * @param <T>
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T>   Stream<T>         from( T[] array                        ) { return new Stream<T>(         primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Byte>      from( byte[] array                     ) { return new Stream<Byte>(      primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Character> from( char[] array                     ) { return new Stream<Character>( primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Short>     from( short[] array                    ) { return new Stream<Short>(     primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Integer>   from( int[] array                      ) { return new Stream<Integer>(   primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Long>      from( long[] array                     ) { return new Stream<Long>(      primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Float>     from( float[] array                    ) { return new Stream<Float>(     primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Double>    from( double[] array                   ) { return new Stream<Double>(    primitiveArrayToList( array ).iterator() ) ; }

    /**
     *
     * @param array
     * @return
     */
    @SuppressWarnings("unchecked")
    public static       Stream<Boolean>   from( boolean[] array                  ) { return new Stream<Boolean>(   primitiveArrayToList( array ).iterator() ) ; }

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
