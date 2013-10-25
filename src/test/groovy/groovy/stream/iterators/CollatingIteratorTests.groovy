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

package groovy.stream.iterators

class CollatingIteratorTests extends spock.lang.Specification {
    @spock.lang.Unroll( "Collation on #input with len:#len, step:#step and keep:#keep should == #result" )
    def "test collating iterator"() {
        setup:
            def i = new CollatingIterator( input.iterator(), len, step, keep )

        expect:
            i.collect() == result

        where:
            input                   | len | step | keep  | result
            [ 1, null, 2, 3, null ] | 1   | 1    | true  | [ [ 1 ], [ null ], [ 2 ], [ 3 ], [ null ] ]
            [ 1, null, 2, 3, null ] | 2   | 2    | true  | [ [ 1, null ], [ 2, 3 ], [ null ] ]
            [ 1, null, 2, 3, null ] | 3   | 3    | true  | [ [ 1, null, 2 ], [ 3, null ] ]
            [ 1, null, 2, 3, null ] | 3   | 3    | false | [ [ 1, null, 2 ] ]
            [ 1, null, 2, 3, null ] | 2   | 1    | false | [ [ 1, null ], [ null, 2 ], [ 2, 3 ], [ 3, null ] ]
    }

    def "Test NoSuchElementException"() {
        setup:
            def i = new CollatingIterator( [ 1, 2, null ].iterator(), 2 )

        when:
            def result = i.collect()

        then:
            result == [ [ 1, 2 ], [ null ] ]

        when:
            i.next()

        then:
            thrown java.util.NoSuchElementException
    }
}