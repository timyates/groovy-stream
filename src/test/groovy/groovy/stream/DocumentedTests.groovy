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

public class DocumentedTests extends spock.lang.Specification {
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
            def stream = Stream.from 'a'..'c' map { [ idx++, it ] } using idx:0
        when:
            def result = stream.collect()
        then:
            result == [ [0,'a'], [1,'b'], [2,'c'] ]
    }
}