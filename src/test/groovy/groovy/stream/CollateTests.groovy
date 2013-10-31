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

class CollateTests extends spock.lang.Specification {
    def "quick test"() {
        setup:
            def stream = Stream.from 1..10 collate( 3 )
        when:
            def result = stream.collect()
        then:
            result == [ [ 1, 2, 3 ], [ 4, 5, 6 ], [ 7, 8, 9 ], [ 10 ] ]
    }

    def "quick no remaining test"() {
        setup:
            def stream = Stream.from 1..10 collate( 3, false )
        when:
            def result = stream.collect()
        then:
            result == [ [ 1, 2, 3 ], [ 4, 5, 6 ], [ 7, 8, 9 ] ]
    }

    def "quick step test"() {
        setup:
            def stream = Stream.from 1..10 collate( 3, 2 )
        when:
            def result = stream.collect()
        then:
            result == [ [ 1, 2, 3 ], [ 3, 4, 5 ], [ 5, 6, 7 ], [ 7, 8, 9 ], [ 9, 10 ] ]
    }

    def "quick step map test"() {
        setup:
            def stream = Stream.from 1..10 collate( 3, 2 ) map { it.sum() }
        when:
            def result = stream.collect()
        then:
            result == [ 6, 12, 18, 24, 19 ]
    }

    def "Check for NPE bug"() {
        setup:
            def stream = Stream.from 1..9 filter { it % 2 == 0 } collate( 2, false )
        when:
            def result = stream.collect()
        then:
            result == [ [ 2, 4 ], [ 6, 8 ] ]
    }
}