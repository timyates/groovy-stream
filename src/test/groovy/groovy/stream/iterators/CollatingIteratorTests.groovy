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
    CollatingIterator iter

    def setup() {
        iter = new CollatingIterator( [ 1, 2, null ].iterator(), 2 )
    }

    def "collect should return values"() {
        when:
            def result = iter.collect()
        then:
            result == [ [ 1, 2 ], [ null ] ]
    }

    def "call to next with no hasNext should work"() {
        expect:
            iter.next() == [ 1, 2 ]
    }

    def "remove should throw UnsupportedOperationException"() {
        when:
            iter.remove()
        then:
            UnsupportedOperationException ex = thrown()
    }

    def "Exhausted iterator should show hasNext = false"() {
        when:
            def result = iter.collect()
        then:
            iter.hasNext() == false
    }

    def "Call to next on Exhausted iterator should throw NoSuchElementException"() {
        when:
            def result = iter.collect()
            iter.next()
        then:
            NoSuchElementException ex = thrown()
    }

    def "check illegal size"() {
        when:
            def iter = new CollatingIterator( [ 1, null, 2 ].iterator(), -1 )
        then:
            IllegalArgumentException ex = thrown()

    }

    def "check illegal step"() {
        when:
            def iter = new CollatingIterator( [ 1, null, 2 ].iterator(), 1, 0 )
        then:
            IllegalArgumentException ex = thrown()

    }

    def "check size and step"() {
        when:
            def iter = new CollatingIterator( [ 1, null, 2 ].iterator(), 2, 1 )
            def result = iter.collect()
        then:
            result == [ [ 1, null ], [ null, 2 ], [ 2 ] ]
    }

    def "check size and keep"() {
        when:
            def iter = new CollatingIterator( [ 1, null, 2 ].iterator(), 2, false )
            def result = iter.collect()
        then:
            result == [ [ 1, null ] ]
    }
}