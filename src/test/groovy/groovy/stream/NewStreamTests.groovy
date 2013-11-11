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

package groovy.stream

import groovy.stream.Stream

class NewStreamTests extends spock.lang.Specification {
    def "test simple stream"() {
        setup:
            def s = Stream.from { 1 }

        when:
            def result = s.take( 5 ).collect()

        then:
            result == [ 1, 1, 1, 1, 1 ]
    }

    def "test simple mapping stream"() {
        setup:
            def s = Stream.from { 1 }.map { it * 2 }

        when:
            def result = s.take( 5 ).collect()

        then:
            result == [ 2, 2, 2, 2, 2 ]
    }

    def "test simple termination"() {
        setup:
            def s = Stream.from( [ 1, 2, 3 ] ).until { it > 1 }

        when:
            def result = s.take( 5 ).collect()

        then:
            result == [ 1 ]
    }

    def "test index appender"() {
        setup:
            def idx = 0
            def list = [ 1, 2, 3 ]
            def stream = Stream.from list map { [ it, idx++ ] }

        when:
            def result = stream.collect()

        then:
            result == [ [ 1, 0 ], [ 2, 1 ], [ 3, 2 ] ]
    }

    def "test arrays"() {
        setup:
            def idx = 0
            int[] array = 1..3
            def stream = Stream.from array map { [ it, idx++ ] }

        when:
            def result = stream.collect()

        then:
            result == [ [ 1, 0 ], [ 2, 1 ], [ 3, 2 ] ]
    }

    def "Map with transformation and limits"() {
        setup:
            def stream = Stream.from x:1..3, y:1..3 filter { x == y } map { x + y }

        when:
            def result = stream.collect()

        then:
            result == [ 2, 4, 6 ]
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

    def "test 1"() {
        setup:
            def stream = Stream.from 1..3
        when:
            def result = stream.collect()
        then:
            result == [ 1, 2, 3 ]
    }

    def "test 2"() {
        setup:
            def stream = Stream.from x:1..3, y:'a'..'b'
        when:
            def result = stream.collect()
        then:
            result == [ [x:1,y:'a'], [x:1,y:'b'], [x:2,y:'a'], [x:2,y:'b'], [x:3,y:'a'], [x:3,y:'b'] ]
    }

    def "test 3"() {
        setup:
            def stream = Stream.from x:1..3, y:'a'..'b' filter { x % 2 == 0 }
        when:
            def result = stream.collect()
        then:
            result == [ [x:2,y:'a'], [x:2,y:'b'] ]
    }

    def "test 4"() {
        setup:
            def stream = Stream.from x:1..3, y:'a'..'b' map { [ x:x*2, y:"letter $y" ] }
        when:
            def result = stream.collect()
        then:
            result == [ [x:2,y:'letter a'], [x:2,y:'letter b'],
                        [x:4,y:'letter a'], [x:4,y:'letter b'],
                        [x:6,y:'letter a'], [x:6,y:'letter b'] ]
    }

    def "test 5"() {
        setup:
            def idx = 0
            def stream = Stream.from 'a'..'c' map { [ idx++, it ] }
        when:
            def result = stream.collect()
        then:
            result == [ [0,'a'], [1,'b'], [2,'c'] ]
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

    def "unfilteredIndex map test"() {
        setup:
            def index = 0
            def stream = Stream.from 1..4 tap { index = it } map { it * index }
        when:
            def result = stream.collect()
        then:
            result == [ 0, 2, 6, 12 ]
    }

    def "unfilteredIndex filter test"() {
        setup:
            def index = 0
            def stream = Stream.from 1..4 tap { index = it } filter { 2 != index }
        when:
            def result = stream.collect()
        then:
            result == [ 1, 2, 4 ]
    }

    def "unfilteredIndex until test"() {
        setup:
            def index = 0
            def stream = Stream.from 1..4 tap { index = it } until { index == 2 }
        when:
            def result = stream.collect()
        then:
            result == [ 1, 2 ]
    }
}