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
 * Streams may be filtered using the <code>where</code> method which takes a Closure
 * returning Groovy Truth:
 *
 * <pre>
 *   def stream = Stream.from x:1..3, y:'a'..'b' where { x % 2 == 0 }
 *   assert [ [x:2,y:'a'], [x:2,y:'b'] ] == stream.collect()
 * </pre>
 *
 * It is possible to use the where block to stop a Stream by returning the
 * magic variable <code>STOP</code>:
 *
 * <pre>
 *   Stream s = Stream.from { 1 } where { idx < 5 ?: STOP } transform { idx++ ; it } using idx:0
 *   // The length of this is 1 greater than expected as transform is executed
 *   // (and so idx is updated) AFTER the where condition has already passed the
 *   // last element.
 *   assert s.collect().size() == 6
 * </pre>
 *
 * It is also possible to transform the returned values by using a <code>transform</code> Closure:
 *
 * <pre>
 *   def stream = Stream.from x:1..3, y:'a'..'b' transform { [ x:x*2, y:"letter $y" ] }
 *   assert [ [x:2,y:'letter a'], [x:2,y:'letter b'],
 *            [x:4,y:'letter a'], [x:4,y:'letter b'],
 *            [x:6,y:'letter a'], [x:6,y:'letter b'] ] == stream.collect()
 * </pre>
 *
 * Finally, it is possible to supply a map of variables to the <code>using</code> Closure
 * which may then be used in any of the components of the Stream setup (though
 * may only be modified in the <code>transform</code> closure above):
 *
 * <pre>
 *   Stream s = Stream.from 'a'..'c' transform { [ idx++, it ] } using idx:0
 *   assert s.collect() == [ [0,'a'], [1,'b'], [2,'c'] ]
 * </pre>
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
   * @return A Stream that will iterate over the iterables
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
   * @return A Stream that will iterate and return the result of Closure.call() each step
   */
  public static Stream from( Closure closure ) {
    new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( closure, { true }, { it }, [:] ) )
  }

  /**
   * The starting point for a Stream taking an Iterator or Iterable object.
   *
   * @param other The Iterable or Iterator to use for the Stream
   * @return A Stream that will iterate over the passed object
   */
  public static Stream from( other ) {
    new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( { other }, { true }, { it }, [:] ) )
  }

  public Stream where( Closure where ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, where, wrapped.transform, wrapped.using ) :
              new StreamImpl( wrapped.definition, where, wrapped.transform, wrapped.using )
    this
  }
  
  public Stream transform( Closure transform ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, wrapped.condition, transform, wrapped.using ) :
              new StreamImpl( wrapped.definition, wrapped.condition, transform, wrapped.using )
    this
  }
  
  public Stream using( Map using ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, wrapped.condition, wrapped.transform, using ) :
              new StreamImpl( wrapped.definition, wrapped.condition, wrapped.transform, using )
    this
  }
}