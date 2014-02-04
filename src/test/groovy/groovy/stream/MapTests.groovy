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

class MapTests extends spock.lang.Specification {
    def "Simple Map"() {
        setup:
            def stream = Stream.from x:1..2, y:1..2

        when:
            def result = stream.collect()

        then:
            result == [[x:1, y:1], [x:1, y:2], [x:2, y:1], [x:2, y:2]]
    }

    def "Map with limits"() {
        setup:
            def stream = Stream.from x:1..2, y:1..2 filter { x == y }

        when:
            def result = stream.collect()

        then:
            result == [ [ x:1, y:1 ], [ x:2, y:2 ] ]
    }

    def "Map with transformation"() {
        setup:
            def stream = Stream.from x:1..2, y:1..2 map { x + y }

        when:
            def result = stream.collect()

        then:
            result == [ 2, 3, 3, 4 ]
    }

    def "Map with transformation and limits"() {
        setup:
            def stream = Stream.from x:1..3, y:1..3 filter { x == y } map { x + y }

        when:
            def result = stream.collect()

        then:
            result == [ 2, 4, 6 ]
    }

    def "Issue #3 - Where with static bindings..."() {
        setup:
            def x = 0
            // Map is now passed in as parameter to filter closure so we can get at it
            // { x == 1 } will always resolve to false as x is 0 in static binding
            def stream = Stream.from x:1..3, y:1..3 filter { it.x == 1 } 

        when:
            def result = stream.collect()

        then:
            result == [ [ x:1,y:1 ], [x:1,y:2], [x:1,y:3 ] ]
    }

    def 'basic flatMap'() {
        setup:
            def stream = Stream.from x:1..3, y:1..3 filter { x == y } flatMap { [ x ] * y }

        when:
            def result = stream.collect()

        then:
            result == [ 1, 2, 2, 3, 3, 3 ]
    }

    def 'basic flatMap with filter'() {
        setup:
            def stream = Stream.from x:1..3, y:1..3 filter { x == y } flatMap { [ x ] * y } filter { it % 2 }

        when:
            def result = stream.collect()

        then:
            result == [ 1, 3, 3, 3 ]
    }

    def 'check empty lists'() {
        setup:
            def stream = Stream.from( [ [], 1..3, [], 4..6, [] ] ).flatMap { it }

        when:
            def result = stream.collect()

        then:
            result == [ 1, 2, 3, 4, 5, 6 ]
    }
}
