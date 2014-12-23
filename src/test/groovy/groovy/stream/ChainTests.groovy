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

class ChainTests extends spock.lang.Specification {
    def "filter map filter chain"() {
        setup:
            def stream = Stream.from 1..10 filter { it % 2 } map { it * 2 } filter { it % 3 }
        when:
            def result = stream.collect()
        then:
            result == [ 2, 10, 14 ]
    }

    def "map map filter chain"() {
        setup:
            def stream = Stream.from( x:1..2, y:3..4 )
                               .map { [ x: x, y: y, z: x + y ] }
                               .map { [ diff: Math.abs( x - y ), sum: z ] }
                               .filter { diff > 2 }
        when:
            def result = stream.collect()
        then:
            result == [ [ diff: 3, sum: 5 ] ]
    }

    def 'Repeating test'() {
        setup:
            def fns = Stream.from([{a -> a * 2}, {a -> a + 2}]).repeat()
            def num = Stream.from 1..10

        when:
            def result = num.zip(fns, {a, b -> b(a)}).collect()
        then:
            result == [2,4,6,6,10,8,14,10,18,12]
    }

    def 'Limited Repeating test'() {
        setup:
            // Only 6 elements in fns
            def fns = Stream.from([{a -> a * 2}, {a -> a + 2}]).repeat(2)
            def num = Stream.from 1..10

        when:
            def result = num.zip(fns, {a, b -> b(a)}).collect()
        then:
            result == [2,4,6,6]
    }
}