/*
 * Copyright 2013 the original author or authors.
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

import java.io.BufferedReader ;

import java.util.Iterator;
import java.util.Map;

public class StreamExtension {
    public static <T>   Stream<T>         toStream( Closure<T>                   delegate ) { return Stream.from( delegate ) ; }
    public static <T>   Stream<T>         toStream( Iterator<T>                  delegate ) { return Stream.from( delegate ) ; }
    public static <T>   Stream<T>         toStream( Iterable<T>                  delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<String>    toStream( BufferedReader               delegate ) { return Stream.from( delegate ) ; }
    public static <K,V> Stream<Map<K,V>>  toStream( Map<K,? extends Iterable<V>> delegate ) { return Stream.from( delegate ) ; }
    public static <T>   Stream<T>         toStream( T[]                          delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Byte>      toStream( byte[]                       delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Character> toStream( char[]                       delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Short>     toStream( short[]                      delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Integer>   toStream( int[]                        delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Long>      toStream( long[]                       delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Float>     toStream( float[]                      delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Double>    toStream( double[]                     delegate ) { return Stream.from( delegate ) ; }
    public static       Stream<Boolean>   toStream( boolean[]                    delegate ) { return Stream.from( delegate ) ; }
}