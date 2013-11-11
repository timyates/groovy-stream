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

class WithIndexTests extends spock.lang.Specification {
    def "Test mapWithIndex"() {
        setup:
            def instream  = Stream.from 1..3

        when:
            def result = instream.mapWithIndex { it, idx -> it * idx }.collect()

        then:
            result == [ 0, 2, 6 ]
    }

    def "Test mapWithIndex2"() {
        setup:
            def instream  = Stream.from x:1..3, y:8..10

        when:
            def result = instream.mapWithIndex { it, idx -> ( y - x ) * idx }.collect()

        then:
            // ( 8  - 1 ) * 0 == 0
            // ( 9  - 1 ) * 1 == 8
            // ( 10 - 1 ) * 2 == 18
            // ( 8  - 2 ) * 3 == 18
            // ( 9  - 2 ) * 4 == 28
            // ( 10 - 2 ) * 5 == 40
            // ( 8  - 3 ) * 6 == 30
            // ( 9  - 3 ) * 7 == 42
            // ( 10 - 3 ) * 8 == 56
            result == [ 0, 8, 18, 18, 28, 40, 30, 42, 56 ]
    }

    def "Test filterWithIndex"() {
        setup:
            def instream  = Stream.from x:1..3, y:8..10

        when:
            def result = instream.mapWithIndex { it, idx -> ( y - x ) * idx }
                                 .filterWithIndex { it, idx -> ( it + idx ) % 9 }
                                 .collect()

        then:
            // ( 8  - 1 ) * 0 == 0  + 0 == 0    X
            // ( 9  - 1 ) * 1 == 8  + 1 == 9    X 
            // ( 10 - 1 ) * 2 == 18 + 2 == 20
            // ( 8  - 2 ) * 3 == 18 + 3 == 21
            // ( 9  - 2 ) * 4 == 28 + 4 == 32
            // ( 10 - 2 ) * 5 == 40 + 5 == 45   X
            // ( 8  - 3 ) * 6 == 30 + 6 == 36   X
            // ( 9  - 3 ) * 7 == 42 + 7 == 49
            // ( 10 - 3 ) * 8 == 56 + 8 == 64
            result == [ 18, 18, 28, 42, 56 ]
    }
}