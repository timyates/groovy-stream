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

class ListTests extends spock.lang.Specification {
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

    def "test removal via limitediterator"() {
        setup:
            def data = [ 1, 2, 3, 4, 5 ]
            def stream = Stream.from data take 3

        when:
            def result = stream.collect()
            stream.remove()

        then:
            result == [  1, 2, 3 ]
            data == [ 1, 2, 4, 5 ]
    }
}