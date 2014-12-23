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
import groovy.stream.iterators.java.* ;
import groovy.stream.iterators.groovy.* ;
import groovy.stream.functions.Function ;
import groovy.stream.functions.Function2 ;
import groovy.stream.functions.Predicate ;
import groovy.stream.functions.IndexedFunction ;
import groovy.stream.functions.IndexedFunction2 ;
import groovy.stream.functions.IndexedPredicate ;

import java.io.BufferedReader ;

import java.lang.reflect.Array ;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;

import java.util.jar.JarEntry ;
import java.util.jar.JarFile ;

import java.util.zip.ZipEntry ;
import java.util.zip.ZipFile ;


/**
 *
 * @author Tim Yates
 * @param <T> the type of each element returned from the Stream.
 */
public class Stream<T> implements Iterator<T> {
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
     * Filter the current stream, passing each element through a predicate filter (Java friendly version).
     *
     * @param predicate A {@link Predicate} instance returning {@code true} from it's {@code call} method
     *                  if the element is to be included.
     * @return A new {@code Stream} wrapping a {@link FilteringIteratorForPredicate}
     */
    public Stream<T> filter( Predicate<T> predicate ) {
        return new Stream<T>( new FilteringIteratorForPredicate<T>( iterator, predicate ) ) ;
    }

    /**
     * Filter the current stream, passing each element and it's index through a predicate filter.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..3 )
     *                .filterWithIndex { it, index -&gt; index % 2 == 1 }
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
     * Filter the current stream, passing each element and its index through a predicate filter (Java friendly version).
     *
     * @param predicate An {@link IndexedPredicate} instance returning {@code true} from it's {@code call} method
     *                  if the element is to be included.
     * @return A new {@code Stream} wrapping a {@link FilteringIteratorForIndexedPredicate}
     */
    public Stream<T> filterWithIndex( IndexedPredicate<T> predicate ) {
        return new Stream<T>( new FilteringIteratorForIndexedPredicate<T>( iterator, predicate ) ) ;
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
     * @param <U> The type of the new Stream.
     * @param map A single parameter closure to pass the element through,
     *            returning a new Collection of elements to iterate.
     * @return A new {@code Stream} wrapping a {@link FlatMapIterator}
     */
    public <U> Stream<U> flatMap( Closure<? extends Collection<U>> map ) { 
        return new Stream<U>( new FlatMapIterator<T,U>( iterator, map, false ) ) ;
    }

    /**
     * Takes a {@link Function} that returns a {@code Collection}.  Each element
     * in this {@code Collection} is passed on in turn, before the next element is
     * fetched from upstream, and the {@link Function} is executed again.
     *
     * @param <U> The type of the new Stream.
     * @param map A single {@link Function} to pass the element through, returning a
     *            new Collection of elements to iterate.
     * @return A new {@code Stream} wrapping a {@link FlatMapIteratorForFunction}
     */
    public <U> Stream<U> flatMap( Function<T,? extends Collection<U>> map ) {
        return new Stream<U>( new FlatMapIteratorForFunction<T,U>( iterator, map ) ) ;
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
     *                .flatMapWithIndex { it, index -&gt; [ it ] * index }
     *                .collect() == [ 2, 3, 3 ]
     * </pre>
     *
     * @param <U> The type of the new Stream.
     * @param map A two parameter closure to pass the element and it's index through,
     *            returning a new Collection of elements to iterate.
     * @return A new {@code Stream} wrapping a {@link FlatMapIterator}
     */
    public <U> Stream<U> flatMapWithIndex( Closure<? extends Collection<U>> map ) { 
        return new Stream<U>( new FlatMapIterator<T,U>( iterator, map, true ) ) ;
    }

    /**
     * Takes an {@link IndexedFunction} that returns a {@code Collection}.  Each element
     * in this {@code Collection} is passed on in turn, before the next element is
     * fetched from upstream, and the {@link IndexedFunction} is executed again.
     *
     * @param <U> The type of the new Stream.
     * @param map A single {@link IndexedFunction} to pass the element and its index through,
     *            returning a new Collection of elements to iterate.
     * @return A new {@code Stream} wrapping a {@link FlatMapIteratorForIndexedFunction}
     */
    public <U> Stream<U> flatMapWithIndex( IndexedFunction<T,? extends Collection<U>> map ) {
        return new Stream<U>( new FlatMapIteratorForIndexedFunction<T,U>( iterator, map ) ) ;
    }

    /**
     * Inspect every value in the {@code Stream} and pass it on unmodified.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def list = []
     *   assert Stream.from( 1..3 )
     *                .tap { list &lt;&lt; it }
     *                .collect() == [ 1, 2, 3 ]
     *   assert list == [ 1, 2, 3 ]
     * </pre>
     *
     * @param output The {@code Closure} to be called for every element
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tap( Closure<Void> output ) { return tapEvery( 1, output ) ; }

    /**
     * Inspect every value in the {@code Stream} and pass it on.
     *
     * @see #tap(groovy.lang.Closure)
     * @param output The {@code Function} to be called for every element
     * @return A new {@code Stream} wrapping a {@link TapIteratorForFunction}
     */
    public Stream<T> tap( Function<T,Void> output ) { return tapEvery( 1, output ) ; }

    /**
     * Inspect the every nth value in the {@code Stream} and pass it on unmodified.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def list = []
     *   assert Stream.from( 1..3 )
     *                .tapEvery( 2 ) { list &lt;&lt; it }
     *                .collect() == [ 1, 2, 3 ]
     *   assert list == [ 2 ]
     * </pre>
     *
     * @param n the elements to inspect
     * @param output The {@code Closure} to be called for every nth element
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tapEvery( int n, Closure<Void> output ) {
        return new Stream<T>( new TapIterator<T>( iterator, n, false, output ) ) ;
    }

    /**
     * Inspect the every nth value in the {@code Stream} and pass it on.
     *
     * @see #tapEvery(int, groovy.lang.Closure)
     * @param n the elements to inspect
     * @param output The {@code Function} to be called for every nth element
     * @return A new {@code Stream} wrapping a {@link TapIteratorForFunction}
     */
    public Stream<T> tapEvery( int n, Function<T,Void> output ) {
        return new Stream<T>( new TapIteratorForFunction<T>( iterator, n, output ) ) ;
    }

    /**
     * Inspect every value in the {@code Stream} with its {@code index} and pass it on unmodified.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def list = []
     *   assert Stream.from( 1..3 )
     *                .tapWithIndex { it, idx -&gt; list &lt;&lt; [ (it):idx ] }
     *                .collect() == [ 1, 2, 3 ]
     *   assert list == [ [ 1:0 ], [ 2:1 ], [ 3:2 ] ]
     * </pre>
     *
     * @param output The closure to call for each element in the Stream.
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tapWithIndex( Closure<Void> output ) { return tapEveryWithIndex( 1, output ) ; }

    /**
     * Inspect every value in the {@code Stream} with its {@code index} and pass it on unmodified.
     *
     * @see #tapWithIndex(groovy.lang.Closure)
     * @param output The {@link IndexedFunction} to call for each element in the Stream.
     * @return A new {@code Stream} wrapping a {@link TapIteratorForIndexedFunction}
     */
    public Stream<T> tapWithIndex( IndexedFunction<T,Void> output ) { return tapEveryWithIndex( 1, output ) ; }

    /**
     * Inspect the every nth value in the {@code Stream} with its index and pass it on unmodified.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def list = []
     *   assert Stream.from( 1..3 )
     *                .tapEveryWithIndex( 2 ) { it, index -&gt; list &lt;&lt; [ (it):index ] }
     *                .collect() == [ 1, 2, 3 ]
     *   assert list == [ [2:1] ]
     * </pre>
     *
     * @param n the elements to inspect
     * @param output The {@link Closure} to be called for every nth element
     * @return A new {@code Stream} wrapping a {@link TapIterator}
     */
    public Stream<T> tapEveryWithIndex( int n, Closure<Void> output ) {
        return new Stream<T>( new TapIterator<T>( iterator, n, true, output ) ) ;
    }

    /**
     * Inspect the every nth value in the {@code Stream} with its index and pass it on unmodified.
     *
     * @see #tapEveryWithIndex(int, groovy.lang.Closure)
     * @param n the elements to inspect
     * @param output The {@link IndexedFunction} to be called for every nth element
     * @return A new {@code Stream} wrapping a {@link TapIteratorForIndexedFunction}
     */
    public Stream<T> tapEveryWithIndex( int n, IndexedFunction<T,Void> output ) {
        return new Stream<T>( new TapIteratorForIndexedFunction<T>( iterator, n, output ) ) ;
    }

    /**
     * Maps the elements of a {@code Stream} to a new value as they are requested. Each
     * element is passed in to a one arg closure, and the result of the {@link Closure}
     * is returned as the next element in the {@code Stream}. The element is also
     * set as the delegate of the {@link Closure}, so you can access map entries
     * by name.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( x:1..3, y:'a'..'c' )
     *                .map { "$x:$y" }
     *                .collect() == [ "1:a", "1:b", "1:c",
     *                                "2:a", "2:b", "2:c",
     *                                "3:a", "3:b", "3:c" ]
     * </pre>
     *
     * @param <U> The type of the new Stream.
     * @param map The transforming Closure.
     * @return A new {@code Stream} wrapping a {@link TransformingIterator}
     */
    public <U> Stream<U> map( Closure<U> map ) {
        return new Stream<U>( new TransformingIterator<T,U>( iterator, map, false ) ) ;
    }

    /**
     * Maps the elements of a {@code Stream} to a new value as they are requested. Each
     * element is passed in to a {@link Function}, the result of which is returned as the
     * next element in the {@code Stream}.
     *
     * @see #map(groovy.lang.Closure)
     * @param <U> The type of the new Stream.
     * @param map The transforming {@link Function}.
     * @return A new {@code Stream} wrapping a {@link TransformingIteratorForFunction}
     */
    public <U> Stream<U> map( Function<T,U> map ) {
        return new Stream<U>( new TransformingIteratorForFunction<T,U>( iterator, map ) ) ;
    }

    /**
     * Maps the elements of a {@code Stream} to a new value as they are requested. Each
     * element plus an index is passed in to a two arg closure, and the result of
     * the {@link Closure} is returned as the next element in the {@code Stream}.
     * The element is also set as the delegate of the {@link Closure}, so you can
     * access map entries by name.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( x:1..3, y:'a'..'c' )
     *                .mapWithIndex { it, idx -&gt; "${x}:${it.y}:${idx}" }
     *                .collect() == [ "1:a:0", "1:b:1", "1:c:2",
     *                                "2:a:3", "2:b:4", "2:c:5",
     *                                "3:a:6", "3:b:7", "3:c:8" ]
     * </pre>
     *
     * @param <U> The type of the new Stream.
     * @param map The transforming Closure.
     * @return A new {@code Stream} wrapping a {@link TransformingIterator}
     */
    public <U> Stream<U> mapWithIndex( Closure<U> map ) {
        return new Stream<U>( new TransformingIterator<T,U>( iterator, map, true ) ) ;
    }

    /**
     * Maps the elements of a {@code Stream} to a new value as they are requested. Each
     * element plus an index is passed in to an {@link IndexedFunction}, and the result of
     * this is returned as the next element in the {@code Stream}.
     *
     * @see #mapWithIndex(groovy.lang.Closure)
     * @param <U> The type of the new Stream.
     * @param map The transforming {@link IndexedFunction}.
     * @return A new {@code Stream} wrapping a {@link TransformingIteratorForIndexedFunction}
     */
    public <U> Stream<U> mapWithIndex( IndexedFunction<T,U> map ) {
        return new Stream<U>( new TransformingIteratorForIndexedFunction<T,U>( iterator, map ) ) ;
    }

    /**
     * When the {@link Closure} predicate returns {@code true}, the stream is stopped.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..5 )
     *                .until { it &gt; 3 }
     *                .collect() == [ 1, 2, 3 ]
     * </pre>
     *
     * @param predicate The {@link Closure} that stops the Stream when it returns {@code true}.
     * @return A new {@code Stream} wrapping an {@link UntilIterator}
     */
    public Stream<T> until( Closure<Boolean> predicate ) {
        return new Stream<T>( new UntilIterator<T>( iterator, predicate, false ) ) ;
    }

    /**
     * When the {@link Predicate} returns {@code true}, the stream is stopped.
     *
     * @see #until(groovy.lang.Closure)
     * @param predicate The {@link Predicate} that stops the Stream when it returns {@code true}.
     * @return A new {@code Stream} wrapping an {@link UntilIteratorForPredicate}
     */
    public Stream<T> until( Predicate<T> predicate ) {
        return new Stream<T>( new UntilIteratorForPredicate<T>( iterator, predicate ) ) ;
    }

    /**
     * Calls the {@link Closure} predicate with the current element and the index in the stream.
     * When the predicate returns {@code true}, the stream is stopped.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..5 )
     *                .untilWithIndex { it, index -&gt; index == 2 }
     *                .collect() == [ 1, 2 ]
     * </pre>
     *
     * @param predicate The {@link Closure} that stops the Stream when it returns {@code true}.
     * @return A new {@code Stream} wrapping an {@link UntilIterator}
     */
    public Stream<T> untilWithIndex( Closure<Boolean> predicate ) {
        return new Stream<T>( new UntilIterator<T>( iterator, predicate, true ) ) ;
    }

    /**
     * Calls the {@link IndexedPredicate} with the current element and the index in the stream.
     * When the predicate returns {@code true}, the stream is stopped.
     *
     * @param predicate The {@link IndexedPredicate} that stops the Stream when it returns {@code true}.
     * @return A new {@code Stream} wrapping an {@link UntilIteratorForIndexedPredicate}
     */
    public Stream<T> untilWithIndex( IndexedPredicate<T> predicate ) {
        return new Stream<T>( new UntilIteratorForIndexedPredicate<T>( iterator, predicate ) ) ;
    }

    /**
     * Groups a the elements of a {@code Stream} into groups of length {@code size}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..9 )
     *                .collate( 4 )
     *                .collect() == [ [ 1, 2, 3, 4 ], [ 5, 6, 7, 8 ], [ 9 ] ]
     * </pre>
     *
     * @param size the size of each collated group
     * @return A new {@code Stream} wrapping an {@link CollatingIterator}
     */
    public Stream<Collection<T>> collate( int size ) { return collate( size, size, true ) ; }

    /**
     * Groups a the elements of a {@code Stream} into groups of length {@code size}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..9 )
     *                .collate( 4, false )
     *                .collect() == [ [ 1, 2, 3, 4 ], [ 5, 6, 7, 8 ] ]
     * </pre>
     *
     * @param size the size of each collated group
     * @param keepRemainder Should any remaining objects be returned at the end
     * @return A new {@code Stream} wrapping an {@link CollatingIterator}
     */
    public Stream<Collection<T>> collate( int size, boolean keepRemainder ) { return collate( size, size, keepRemainder ) ; }

    /**
     * Groups a the elements of a {@code Stream} into groups of length {@code size}
     * using a step-size of {@code step}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..9 )
     *                .collate( 4, 1 )
     *                .collect() == [ [ 1, 2, 3, 4 ],
     *                                [ 2, 3, 4, 5 ],
     *                                [ 3, 4, 5, 6 ],
     *                                [ 4, 5, 6, 7 ],
     *                                [ 5, 6, 7, 8 ],
     *                                [ 6, 7, 8, 9 ],
     *                                [ 7, 8, 9 ],
     *                                [ 8, 9 ],
     *                                [ 9 ] ]
     * </pre>
     *
     * @param size the size of each collated group
     * @param step How many to increment the window by each turn
     * @return A new {@code Stream} wrapping an {@link CollatingIterator}
     */
    public Stream<Collection<T>> collate( int size, int step ) { return collate( size, step, true ) ; }

    /**
     * Groups a the elements of a {@code Stream} into groups of length {@code size}
     * using a step-size of {@code step}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..9 )
     *                .collate( 4, 1, false )
     *                .collect() == [ [ 1, 2, 3, 4 ],
     *                                [ 2, 3, 4, 5 ],
     *                                [ 3, 4, 5, 6 ],
     *                                [ 4, 5, 6, 7 ],
     *                                [ 5, 6, 7, 8 ],
     *                                [ 6, 7, 8, 9 ] ]
     * </pre>
     *
     * @param size the size of each collated group
     * @param step How many to increment the window by each turn
     * @param keepRemainder Should any remaining objects be returned at the end
     * @return A new {@code Stream} wrapping an {@link CollatingIterator}
     */
    public Stream<Collection<T>> collate( int size, int step, boolean keepRemainder ) {
        return new Stream<Collection<T>>( new CollatingIterator<T>( this.iterator, size, step, keepRemainder ) ) ;
    }

    /**
     * Takes another {@code Iterator} or {@code Stream} and calls the two arg {@code Closure}
     * to zip the two together.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def numbers = Stream.from 1..3
     *   def letters = Stream.from 'a'..'d'
     *  
     *   assert numbers.zip( letters ) { n, l -&gt; "$n:$l" }
     *                .collect() == [ "1:a", "2:b", "3:c" ]
     * </pre>
     * 
     * @param <U> The type of the secondary {@link Iterator}.
     * @param <V> The type of the new {@link Iterator}.
     * @param other The other {@link Iterator}
     * @param map The 2 arg {@link Closure} to call with each next element from the Stream
     * @return A new {@code Stream} wrapping a {@link ZipIterator}
     */
    public <U,V> Stream<V> zip( Iterator<U> other, Closure<V> map ) {
        return new Stream<V>( new ZipIterator<T,U,V>( this.iterator, other, false, map ) ) ;
    }

    public <U,V> Stream<V> zip( Iterable<U> other, Closure<V> map ) {
        return new Stream<V>( new ZipIterator<T,U,V>( this.iterator, other.iterator(), false, map ) ) ;
    }

    /**
     * Takes another {@code Iterator} or {@code Stream} and calls the two arg {@code Function2}
     * to zip the two together.
     *
     * @see #zip(java.util.Iterator, groovy.lang.Closure)
     * @param <U> The type of the secondary {@link Iterator}.
     * @param <V> The type of the new {@link Iterator}.
     * @param other The other {@link Iterator}
     * @param map The 2 arg {@link Function2} to call with each next element from the Stream
     * @return A new {@code Stream} wrapping a {@link ZipIteratorForFunction}
     */
    public <U,V> Stream<V> zip( Iterator<U> other, Function2<T,U,V> map ) {
        return new Stream<V>( new ZipIteratorForFunction<T,U,V>( this.iterator, other, map ) ) ;
    }

    public <U,V> Stream<V> zip( Iterable<U> other, Function2<T,U,V> map ) {
        return new Stream<V>( new ZipIteratorForFunction<T,U,V>( this.iterator, other.iterator(), map ) ) ;
    }

    /**
     * Takes another {@code Iterator} or {@code Stream} and calls the three arg {@code Closure}
     * to zip the two together along with the current index.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def numbers = Stream.from 1..3
     *   def letters = Stream.from 'a'..'d'
     *  
     *   assert numbers.zipWithIndex( letters ) { n, l, i -&gt; "$n:$l:$i" }
     *                .collect() == [ "1:a:0", "2:b:1", "3:c:2" ]
     * </pre>
     * 
     * @param <U> The type of the secondary {@link Iterator}.
     * @param <V> The type of the new {@link Iterator}.
     * @param other The other {@link Iterator}
     * @param map The 3 arg {@link Closure} to call with each next element from the Stream and the
     *            current stream index
     * @return A new {@code Stream} wrapping a {@link ZipIterator}
     */
    public <U,V> Stream<V> zipWithIndex( Iterator<U> other, Closure<V> map ) {
        return new Stream<V>( new ZipIterator<T,U,V>( this.iterator, other, true, map ) ) ;
    }

    public <U,V> Stream<V> zipWithIndex( Iterable<U> other, Closure<V> map ) {
        return new Stream<V>( new ZipIterator<T,U,V>( this.iterator, other.iterator(), true, map ) ) ;
    }

    /**
     * Takes another {@code Iterator} or {@code Stream} and calls the three arg {@link IndexedFunction2}
     * to zip the two together along with the current index.
     *
     * @see #zipWithIndex(java.util.Iterator, groovy.lang.Closure)
     * @param <U> The type of the secondary {@link Iterator}.
     * @param <V> The type of the new {@link Iterator}.
     * @param other The other {@link Iterator}
     * @param map The 3 arg {@link IndexedFunction2} to call with each next element from the Stream and the
     *            current stream index
     * @return A new {@code Stream} wrapping a {@link ZipIteratorForIndexedFunction}
     */
    public <U,V> Stream<V> zipWithIndex( Iterator<U> other, IndexedFunction2<T,U,V> map ) {
        return new Stream<V>( new ZipIteratorForIndexedFunction<T,U,V>( this.iterator, other, map ) ) ;
    }

    public <U,V> Stream<V> zipWithIndex( Iterable<U> other, IndexedFunction2<T,U,V> map ) {
        return new Stream<V>( new ZipIteratorForIndexedFunction<T,U,V>( this.iterator, other.iterator(), map ) ) ;
    }

    /**
     * When this stream completes, repeat it's output endlessly.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def numbers = Stream.from 1..3
     *
     *   // We will just take the first 12 elements of this infinite stream
     *   assert numbers.repeat().take(12).collect() == [1,2,3,1,2,3,1,2,3,1,2,3]
     * </pre>
     *
     * @return A new {@code Stream} wrapping a {@link RepeatingIterator}
     */
    public Stream<T> repeat() {
        return new Stream<T>(new RepeatingIterator<T>(this.iterator));
    }

    /**
     * This stream will repeat <code>count</code> times.
     *
     * If count is <code>0</code>, the Stream will be empty. If <code>1</code>, no repetition will be performed.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   def numbers = Stream.from(['alice', 'bob'])
     *
     *   assert numbers.repeat(2).collect() == ['alice','bob','alice','bob']
     * </pre>
     *
     * @param count The number of times to repeat the element in this Stream once it is exhausted.
     * @return A new {@code Stream} wrapping a {@link RepeatingIterator}
     */
    public Stream<T> repeat(int count) {
        return new Stream<T>(new RepeatingIterator<T>(this.iterator, count));
    }

    /**
     * Limits the {@code Stream} to {@code n} elements.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1..9 )
     *                .take( 3 )
     *                .collect() == [ 1, 2, 3 ]
     * </pre>
     *
     * @param n The number of element to limit the {@code Stream} to.
     * @return A new {@code Stream} wrapping a {@link LimitedIterator}
     */
    public Stream<T> take( int n ) {
        return new Stream<T>( new LimitedIterator<T>( this.iterator, n ) ) ;
    }

    /**
     * Construct a {@code Stream} from a {@link Map} of Iterables.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( a:1..3, b:'a'..'c' )
     *                .collect() == [ [ a:1, b:'a' ],
     *                                [ a:1, b:'b' ],
     *                                [ a:1, b:'c' ],
     *                                [ a:2, b:'a' ],
     *                                [ a:2, b:'b' ],
     *                                [ a:2, b:'c' ],
     *                                [ a:3, b:'a' ],
     *                                [ a:3, b:'b' ],
     *                                [ a:3, b:'c' ] ]
     * </pre>
     *
     * @param <K> They type of the Map keys.
     * @param <V> The type of the Iterable value.
     * @param map The map of Iterables.
     * @return A new {@code Stream} wrapping a {@link MapIterator}.
     */
    public static <K,V> Stream<Map<K,V>> from( Map<K,? extends Iterable<V>> map ) {
        return new Stream<Map<K,V>>( new MapIterator<K,V>( map ) ) ;
    }

    /**
     * Construct a {@code Stream} from another {@code Stream}.
     * 
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( Stream.from( 1..3 ) )
     *                .collect() == [ 1, 2, 3 ]
     * </pre>
     *
     * @param <T> The type of the Stream.
     * @param stream The other {@code Stream}.
     * @return A new {@code Stream} wrapping the iterator of the other {@code Stream}.
     */
    public static <T> Stream<T> from( Stream<T> stream ) {
        return new Stream<T>( stream.iterator ) ;
    }

    /**
     * Construct a {@code Stream} from an {@link Iterable}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( [ 1, 2, 3 ] )
     *                .collect() == [ 1, 2, 3 ]
     * </pre>
     *
     * @param <T> The type of the Iterable.
     * @param iterable The iterable to iterate.
     * @return A new {@code Stream} wrapping the {@code iterable.iterator()}.
     */
    public static <T> Stream<T> from( Iterable<T> iterable ) {
        return new Stream<T>( iterable.iterator() ) ;
    }

    /**
     * Construct a {@code Stream} from an {@link Iterator}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( [ 1, 2, 3 ].iterator() )
     *                .collect() == [ 1, 2, 3 ]
     * </pre>
     *
     * @param <T> The type of the Iterator.
     * @param iterator The iterator to wrap.
     * @return A new {@code Stream} wrapping the iterator.
     */
    public static <T> Stream<T> from( Iterator<T> iterator ) {
        return new Stream<T>( iterator ) ;
    }

    /**
     * Construct a {@code Stream} from a {@link BufferedReader} that iterates the lines in it.
     *
     * @param reader The Reader to iterate lines from
     * @return A new {@code Stream} wrapping a {@link BufferedReaderIterator}.
     */
    public static Stream<String> from( BufferedReader reader ) {
        return new Stream<String>( new BufferedReaderIterator( reader ) ) ;
    }

    /**
     * Construct a {@code Stream} from a {@link ZipFile} that iterates the {@link ZipEntry} objects contained within.
     * 
     * @param file the ZipFile to iterate ZipEntrys from.
     * @return A new {@code Stream} wrapping an {@link EnumerationIterator}.
     */
    public static Stream<ZipEntry> from( ZipFile file ) {
        return new Stream<ZipEntry>( new EnumerationIterator<ZipEntry>( file.entries() ) ) ;
    }

    /**
     * Construct a {@code Stream} from a {@link JarFile} that iterates the {@link JarEntry} objects contained within.
     *
     * @param file the JarFile to iterate JarEntrys from.
     * @return A new {@code Stream} wrapping an {@link EnumerationIterator}.
     */
    public static Stream<JarEntry> from( JarFile file ) {
        return new Stream<JarEntry>( new EnumerationIterator<JarEntry>( file.entries() ) ) ;
    }

    /**
     * Construct a {@code Stream} that for every element, returns the result of calling the {@link Closure}.
     *
     * @param <T> The type of the return value from the Closure.
     * @param closure The closure to call each time an element is requested.
     * @return A new {@code Stream} wrapping an {@link RepeatingClosureIterator}.
     */
    public static <T> Stream<T> from( Closure<T> closure ) {
        return new Stream<T>( new RepeatingClosureIterator<T>( closure ) ) ;
    }

    /**
     * Construct a {@code Stream} that for every element, returns {@code object}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   assert Stream.from( 1 ).take( 3 ).collect() == [ 1, 1, 1 ]
     * </pre>
     *
     * @param <T> The type of the {@code Object}.
     * @param object The object to return each time an element is requested.
     * @return A new {@code Stream} wrapping an {@link RepeatingObjectIterator}.
     */
    public static <T> Stream<T> from( T object ) {
        return new Stream<T>( new RepeatingObjectIterator<T>( object ) ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Object} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   String[] elems = [ 'one', 'two', 'three' ]
     *
     *   assert Stream.from( elems ).collect() == [ 'one', 'two', 'three' ]
     * </pre>
     *
     * @param <T> The type of the array.
     * @param array An array of Object to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> from( T[] array ) {
        return new Stream<T>( primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code byte} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   byte[] elems = [ 127, 128, 129 ]
     *
     *   assert Stream.from( elems ).collect() == [ 127, -128, -127 ]
     * </pre>
     *
     * @param array An array of byte to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Byte> from( byte[] array ) {
        return new Stream<Byte>(      primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Character} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   char[] elems = 'Hello'.toList()
     *
     *   assert Stream.from( elems ).collect() == [ 'H', 'e', 'l', 'l', 'o' ]
     * </pre>
     *
     * @param array An array of char to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Character> from( char[] array ) {
        return new Stream<Character>( primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Short} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   short[] elems = [ 10, 65535, 65536, 65537, 99 ]
     *
     *   assert Stream.from( elems ).collect() == [ 10, -1, 0, 1, 99 ]
     * </pre>
     *
     * @param array An array of short to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Short> from( short[] array ) {
        return new Stream<Short>( primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Integer} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   int[] elems = [ 10, 65535, 65536, 65537, 99 ]
     *
     *   assert Stream.from( elems ).collect() == [ 10, 65535, 65536, 65537, 99 ]
     * </pre>
     *
     * @param array An array of int to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Integer> from( int[] array ) {
        return new Stream<Integer>( primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Long} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   long[] elems = [ 10, 65535, 65536, 65537, 99 ]
     *
     *   assert Stream.from( elems ).collect() == [ 10, 65535, 65536, 65537, 99 ]
     * </pre>
     *
     * @param array An array of long to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Long> from( long[] array ) {
        return new Stream<Long>( primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Float} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   float[] elems = [ 0.1, 0.2, 0.3 ]
     *
     *   assert Stream.from( elems ).collect() == [ 0.1f, 0.2f, 0.3f ]
     * </pre>
     *
     * @param array An array of float to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Float> from( float[] array ) {
        return new Stream<Float>( primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Double} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   double[] elems = [ 0.1, 0.2, 0.3 ]
     *
     *   assert Stream.from( elems ).collect() == [ 0.1, 0.2, 0.3 ]
     * </pre>
     *
     * @param array An array of double to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Double> from( double[] array ) {
        return new Stream<Double>( primitiveArrayToList( array ).iterator() ) ;
    }

    /**
     * Construct a {@code Stream} that iterates every {@code Boolean} in an array. First converts the array to an {@link ArrayList}, then wraps the {@code ArrayList.iterator()}.
     *
     * <pre class="groovyTestCase">
     *   import groovy.stream.*
     *
     *   boolean[] elems = [ true, false ]
     *
     *   assert Stream.from( elems ).collect() == [ true, false ]
     * </pre>
     *
     * @param array An array of boolean to iterate
     * @return A new {@code Stream} wrapping the iterator for the array as a List.
     */
    @SuppressWarnings("unchecked")
    public static Stream<Boolean> from( boolean[] array ) {
        return new Stream<Boolean>( primitiveArrayToList( array ).iterator() ) ;
    }

    @Override public T next() {
        return iterator.next() ;
    }
    @Override public boolean hasNext() {
        return iterator.hasNext() ;
    }
    @Override public void remove() {
        iterator.remove() ;
    }

    /* Utilities */
    @SuppressWarnings("unchecked")
    private static List primitiveArrayToList( Object array ) {
        int size = Array.getLength( array ) ;
        List list = new ArrayList( size ) ;
        for( int i = 0 ; i < size ; i++ ) {
            list.add( Array.get( array, i ) ) ;
        }
        return list ;
    }
}
