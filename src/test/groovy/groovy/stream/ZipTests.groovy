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

class ZipTests extends spock.lang.Specification {
    def "Test simple zip"() {
        setup:
            def stream1 = Stream.from 1..3
            def stream2 = Stream.from 'a'..'c'

        expect:
            stream1.zip( stream2 ) { a, b ->
                [ a, b ]
            }.collect() == [ [ 1, 'a' ], [ 2, 'b' ], [ 3, 'c' ] ]
    }

    def "Test zip with index"() {
        setup:
            def stream1 = Stream.from 1..3
            def stream2 = Stream.from 'a'..'c'

        expect:
            stream1.zipWithIndex( stream2 ) { a, b, idx ->
                [ idx, a, b ]
            }.collect() == [ [ 0, 1, 'a' ], [ 1, 2, 'b' ], [ 2, 3, 'c' ] ]
    }

    def "Test iterable zip"() {
        setup:
            def stream1 = Stream.from 1..3

        expect:
            stream1.zip( [ 'a', 'b', 'c' ] ) { a, b ->
                [ a, b ]
            }.collect() == [ [ 1, 'a' ], [ 2, 'b' ], [ 3, 'c' ] ]
    }

    def "Test iterable zip with index"() {
        setup:
            def stream1 = Stream.from 1..3

        expect:
            stream1.zipWithIndex( [ 'a', 'b', 'c' ] ) { a, b, idx ->
                [ idx, a, b ]
            }.collect() == [ [ 0, 1, 'a' ], [ 1, 2, 'b' ], [ 2, 3, 'c' ] ]
    }
}