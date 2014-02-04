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

public class IteratorTests extends spock.lang.Specification {
    def "test list iterator"() {
        setup:
            def iterator = [ 1, 2, 3 ].iterator()
            def stream = Stream.from iterator

        when:
            def result = stream.collect()

        then:
            result == [ 1, 2, 3 ]
    }

    def "Test until"() {
        setup:
            def x = 0
            def eternal = [ hasNext:{ true }, next:{ x++ } ] as Iterator
            def stream = Stream.from eternal until { it >= 5 }

        when:
            def result = stream.collect()

        then:
            result == [ 0, 1, 2, 3, 4 ]
            eternal.next() == 6
            stream.hasNext() == false
    }

    def "Test until with large ranges"() {
        setup:
            // Ranges as of Groovy 1.8.6 can't be > Integer.MAX_VALUE
            // in length else iterator() fails
            def eternal = (1..2147483647).iterator()
            def stream = Stream.from eternal until { it >= 5 }

        when:
            def result = stream.collect()

        then:
            result == [ 1, 2, 3, 4 ]
            eternal.next() == 6
            stream.hasNext() == false
    }

    def "Stream of randoms"() {
        setup:
            def rnd = new Random( 10 )
            def rndIterator = [ hasNext:{ true }, next:{ rnd.nextDouble() } ] as Iterator
            def stream = Stream.from rndIterator filter { it < 0.5 } map { ( it * 100 ) as Integer }

        when:
            def result = stream.take( 2 ).collect()

        then:
            result == [ 25, 5 ]
    }

    def "Github issue #11: Just calling next() causes NPE"() {
        setup:
            def iter = [ hasNext:{ true }, next:{ 1 } ] as Iterator
            def stream = Stream.from iter

        when:
            def result = stream.next()

        then:
            result == 1
    }
}