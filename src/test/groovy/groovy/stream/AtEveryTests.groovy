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

class AtEveryTests extends spock.lang.Specification {
    def "test every time"() {
        setup:
            def list = []
            def stream = Stream.from( 1..10 ).filter { it % 2 }
                                             .atEvery { idx -> list << idx }
                                             .map { it * 2 }
                                             .filter { it % 3 }
        when:
            def result = stream.collect()
        then:
            result == [ 2, 10, 14 ]
            list == [ 1, 2, 3, 4, 5 ]
    }

    def "test every 2 times"() {
        setup:
            def list = []
            def stream = Stream.from( 1..10 ).filter { it % 2 }
                                             .atEvery( 2 ) { idx -> list << idx }
                                             .map { it * 2 }
                                             .filter { it % 3 }
        when:
            def result = stream.collect()
        then:
            result == [ 2, 10, 14 ]
            list == [ 2, 4 ]
    }

    def "test every time with object"() {
        setup:
            def list = []
            def stream = Stream.from( 1..10 ).filter { it % 2 }
                                             .atEvery { idx, obj -> list << [ idx, obj ] }
                                             .map { it * 2 }
                                             .filter { it % 3 }
        when:
            def result = stream.collect()
        then:
            result == [ 2, 10, 14 ]
            list == [ [ 1, 1 ], [ 2, 3 ], [ 3, 5 ], [ 4, 7 ], [ 5, 9 ] ]
    }

    def "test every 2 times with object"() {
        setup:
            def list = []
            def stream = Stream.from( 1..10 ).filter { it % 2 }
                                             .atEvery( 2 ) { idx, obj -> list << [ idx, obj ] }
                                             .map { it * 2 }
                                             .filter { it % 3 }
        when:
            def result = stream.collect()
        then:
            result == [ 2, 10, 14 ]
            list == [ [ 2, 3 ], [ 4, 7 ] ]
    }
}