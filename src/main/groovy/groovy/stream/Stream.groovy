/*
 * Copyright 2012 the original author or authors.
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
package groovy.stream

/**
 * The Stream class is the entry point for Lazy sequence generation.
 *
 * A Stream may be constructed with either an Iterator:
 *
 * <pre>
 *   List a = [ 1, 2, 3 ]
 *   Stream.from a.iterator()
 * </pre>
 *
 * or an Iterable:
 *
 * <pre>
 *   def a = 1..4
 *   Stream.from a
 * </pre>
 *
 * or a closure
 *
 * <pre>
 *   Stream.from { 1 } // Infinite Stream!
 * </pre>
 *
 * or finally, a Map of Iterables:
 *
 * <pre>
 *   Stream.from x:1..4, y:'a'..'z'
 * </pre>
 *
 * Any of these return a Stream which is a implementation of Iterator, and so
 * may be treated as such:
 *
 * <pre>
 *   def stream = Stream.from 1..3
 *   assert [ 1, 2, 3 ] == stream.collect()
 *
 *   stream = Stream.from x:1..3, y:'a'..'b'
 *   assert [ [x:1,y:'a'], [x:1,y:'b'],
 *            [x:2,y:'a'], [x:2,y:'b'],
 *            [x:3,y:'a'], [x:3,y:'b'] ] == stream.collect()
 * </pre>
 *
 * Streams may be filtered using the <code>filter</code> method which takes a Closure
 * returning Groovy Truth:
 *
 * <pre>
 *   def stream = Stream.from x:1..3, y:'a'..'b' filter { x % 2 == 0 }
 *   assert [ [x:2,y:'a'], [x:2,y:'b'] ] == stream.collect()
 * </pre>
 *
 * It is possible to use the where block to stop a Stream by returning the
 * magic variable <code>STOP</code>:
 *
 * <pre>
 *   Stream s = Stream.from { 1 } filter { idx < 5 ?: STOP } map { idx++ ; it } using idx:0
 *   // The length of this is 1 greater than expected as transform is executed
 *   // (and so idx is updated) AFTER the where condition has already passed the
 *   // last element.
 *   assert s.collect().size() == 6
 * </pre>
 *
 * It is also possible to transform the returned values by using a <code>map</code> Closure:
 *
 * <pre>
 *   def stream = Stream.from x:1..3, y:'a'..'b' map { [ x:x*2, y:"letter $y" ] }
 *   assert [ [x:2,y:'letter a'], [x:2,y:'letter b'],
 *            [x:4,y:'letter a'], [x:4,y:'letter b'],
 *            [x:6,y:'letter a'], [x:6,y:'letter b'] ] == stream.collect()
 * </pre>
 *
 * Finally, it is possible to supply a map of variables to the <code>using</code> Closure
 * which may then be used in any of the components of the Stream setup (though
 * may only be modified in the <code>map</code> closure above):
 *
 * <pre>
 *   Stream s = Stream.from 'a'..'c' map { [ idx++, it ] } using idx:0
 *   assert s.collect() == [ [0,'a'], [1,'b'], [2,'c'] ]
 * </pre>
 *
 * Streams may only have one <code>using</code>, <code>map</code>, or
 * <code>where</code> block.  Calling these methods multiple times with result
 * in the original blocks getting overwritten by the new ones.
 *
 * @author Tim Yates
 */
public class Stream<T> implements StreamInterface<T> {
  private static enum StreamType { MAP, OTHER }
  private StreamInterface wrapped
  private StreamType type

  /**
   * @see java.util.Iterator#hasNext()
   */
  public  boolean    hasNext()        { wrapped.hasNext()   }
  /**
   * @see java.util.Iterator#next()
   */
  public  T          next()           { wrapped.next()      }
  /**
   * Unavailable, this will throw an <code>UnsupportedOperationException</code>
   * @see java.util.Iterator#remove()
   * @see java.lang.UnsupportedOperationException
   */
  public  void       remove()         { wrapped.remove()    }

  /**
   * Has the Stream been completely exhausted?
   * @return true if the Stream is at an end
   */
  public  boolean    isExhausted()    { wrapped.exhausted   }
  /**
   * Get the current index of the Stream (starting from 0 for the first element)
   * @return the current index
   */
  public  int        getStreamIndex() { wrapped.streamIndex }

  /**
   * The starting point for a Stream taking a Map of Iterables to
   * lazily return.  The Stream will return all combinations of this map,
   * incrementing the right-hand Map entry first (until exhausted) then
   * advancing the next entry and starting again (until all Iterables are
   * exhausted)
   *
   * @param iterables The Map of Iterables
   * @return A Stream that will iterate over the iterables, with
   *         <code>filter</code> set to <code>{true}</code>,
   *         <code>map</code> set to <code>{it}</code> and
   *         <code>using</code> set to the empty Map.
   */
  public static Stream from( Map iterables ) {
    new Stream( type:StreamType.MAP, wrapped:new MapStream( { iterables }, { true }, { it }, [:] ) )
  }

  /**
   * The starting point for a Stream taking a Closure.  The result of
   * calling the Closure will be returned for each iteration.  This may
   * result in an Infinite Stream which can be stopped by returning STOP from
   * a additional where clause (see example in main documentation for Stream)
   *
   * @param closure The Closure to call for each returned element
   * @return A Stream that will iterate and return the result of Closure.call()
   *         each step, with <code>filter</code> set to <code>{true}</code>,
   *         <code>map</code> set to <code>{it}</code> and
   *         <code>using</code> set to the empty Map.
   */
  public static Stream from( Closure closure ) {
    new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( closure, { true }, { it }, [:] ) )
  }

  /**
   * The starting point for a Stream taking an Iterator or Iterable object.
   *
   * @param other The Iterable or Iterator to use for the Stream
   * @return A Stream that will iterate over the passed object, with
   *         <code>filter</code> set to <code>{true}</code>,
   *         <code>map</code> set to <code>{it}</code> and
   *         <code>using</code> set to the empty Map.
   */
  public static Stream from( other ) {
    new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( { other }, { true }, { it }, [:] ) )
  }

  /**
   * The starting point for a Stream taking an array.  This will make a copy of the array
   * (into a new List instance), and then generate the Stream from this new List.
   *
   * @param array The The array to use for the stream
   * @return A Stream that will iterate over the passed array, with
   *         <code>filter</code> set to <code>{true}</code>,
   *         <code>map</code> set to <code>{it}</code> and
   *         <code>using</code> set to the empty Map.
   */
  public static <T> Stream from( T[] array       ) { fromArray( array ) }
  public static     Stream from( byte[] array    ) { fromArray( array ) }
  public static     Stream from( char[] array    ) { fromArray( array ) }
  public static     Stream from( short[] array   ) { fromArray( array ) }
  public static     Stream from( int[] array     ) { fromArray( array ) }
  public static     Stream from( long[] array    ) { fromArray( array ) }
  public static     Stream from( float[] array   ) { fromArray( array ) }
  public static     Stream from( double[] array  ) { fromArray( array ) }
  public static     Stream from( boolean[] array ) { fromArray( array ) }

  private static Stream fromArray( array ) {
    new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( { array.toList() }, { true }, { it }, [:] ) )
  }

  /**
   * A basic filter for the Stream.  Elements in the Stream are passed to this
   * Closure, and only those that cause this closure to return something
   * passing Groovy Truth will be returned from the Stream. For Map based
   * Streams, the current map is set as the Delegate for the closure, so the
   * keys may be accessed directly, but (as with non-map based streams), it is
   * also passed as a parameter to the closure so the map values may be
   * accessed where they are hidden by variables in the static scope.
   *
   * @param predicate The closure to filter the Stream
   * @return A new Stream with this filter assigned.
   */
  public Stream filter( Closure predicate ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, predicate, wrapped.transform, wrapped.using ) :
              new StreamImpl( wrapped.definition, predicate, wrapped.transform, wrapped.using )
    this
  }
  
  /**
   * Set the default transformation for this Stream. Values are passed
   * through this closure before being returned. It is also possible to
   * alter the values in the <code>using</code> Map from within the closure.
   * For Map based Streams, the current map is set as the Delegate for the
   * closure, so the keys may be accessed directly, but (as with non-map based
   * streams), it is also passed as a parameter to the closure so the map
   * values may be accessed where they are hidden by variables in the static
   * scope.
   *
   * @param transform The closure to manipulate the current stream value.
   * @return A new Stream with this transform assigned.
   */
  public Stream map( Closure transform ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, wrapped.condition, transform, wrapped.using ) :
              new StreamImpl( wrapped.definition, wrapped.condition, transform, wrapped.using )
    this
  }
  
  /**
   * Set the map of values that is accessible to the component parts of the
   * Stream. This map is passed to all of the other Closures used by the Stream
   * and may be modified by any transform Closure that has been set.  When
   * using a Stream over a Map of values, these values will overwrite any
   * values of the initial map with the same key.
   *
   * @param using The map of values.
   * @return A new Stream with this using Map assigned.
   */
  public Stream using( Map using ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, wrapped.condition, wrapped.transform, using ) :
              new StreamImpl( wrapped.definition, wrapped.condition, wrapped.transform, using )
    this
  }
}