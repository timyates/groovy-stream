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

class ExtensionTests extends spock.lang.Specification {
    def 'closure test'() {
        setup:
            def stream = { -> 1 }.toStream()
        when:
            def result = stream.take( 4 ).collect()
        then:
            result == [ 1, 1, 1, 1 ]
    }

    def 'iterator test'() {
        setup:
            def iterator = [ hasNext:{ true }, next:{ 'a' } ] as Iterator
            def stream = iterator.toStream().map { it + 1 }
        when:
            def result = stream.take( 4 ).collect()
        then:
            result == [ 'a1', 'a1', 'a1', 'a1' ]
    }

    def 'range test'() {
        setup:
            def stream = ('a'..'z').toStream().filter { ((int)it[0]) % 2 == 1 }
        when:
            def result = stream.take( 4 ).collect()
        then:
            result == [ 'a', 'c', 'e', 'g' ]
    }

    def 'map test'() {
        setup:
            def stream = [ x:1..3, y:1..3 ].toStream().filter { x == y }
        when:
            def result = stream.take( 4 ).collect()
        then:
            result == [ [x:1,y:1], [x:2,y:2], [x:3,y:3] ]
    }

    def 'iterable test'() {
        setup:
            def stream = [ 1, 2, 3, 4, 5 ].toStream().map { it * x++ } using x:10
        when:
            def result = stream.take( 4 ).collect()
        then:
            result == [ 10, 22, 36, 52 ]
    }

    def "example from http://blog.hartveld.com/2013/03/jdk-8-33-stream-api.html"() {
        setup:
            def blocks = [ [ color: 'red',   weight: 10.5 ],
                           [ color: 'green', weight: 3 ],
                           [ color: 'red',   weight: 1.5 ],
                           [ color: 'blue',  weight: 12 ] ]
            def stream = blocks.toStream()
                               .filter { it.color == 'red' }
                               .map { it.weight }
        when:
            def result = stream.sum()
        then:
            result == 12.0
    }
}
