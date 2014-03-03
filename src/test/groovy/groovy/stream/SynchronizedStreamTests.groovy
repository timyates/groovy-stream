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

package groovy.stream

class SynchronizedStreamTests extends spock.lang.Specification {
    def "Streams derived from a synchronized stream should be synchronized"() {
        setup:
            def s = Stream.from( 1 )
        when:
            def t = s.map { it * 2 }
        then:
            s.synchronized == true
            t.synchronized == true
    }

    def "Should allow multiple threads reading from the map stream"() {
        setup:
            def s = Stream.from( x:1..10, y:1..10 ).asSynchronized()

            def c = []

            def p = { v ->
                synchronized( c ) {
                    c << v
                }
            }

            def g = { name -> new Thread( { -> while( s.hasNext() ) p( s.next() ) } ) }

            def t = [ g( 'a' ), g( 'b' ) ]

        when:
            t*.start()
            t*.join()

        then:
            c.size() == 100
            c.unique().size() == 100
    }

    def "Should allow multiple threads reading from previous streams"() {
        setup:
            def upper = 100
            def counter = Stream.from( 1..upper ).asSynchronized()
            def mapped = Stream.from counter map { it }

            def result = []

            def store = { value ->
                synchronized( result ) {
                    result << value
                }
            }

            def generate = { name, stream ->
                new Thread( { -> while( stream.hasNext() ) store( stream.next() ) } )
            }

            def threads = [ generate( 'a', mapped ),
                            generate( 'b', counter ) ]

        when:
            threads*.start()
            threads*.join()

        then:
            result.size() == upper
            result.unique().size() == upper
    }
}
