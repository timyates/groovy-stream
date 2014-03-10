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

package groovy.stream.iterators.groovy

class ZipIteratorTests extends spock.lang.Specification {
    List a = [ 1, null, 2 ]
    List b = [ 3, 4 ]

    ZipIterator iter

    def setup() {
        iter = new ZipIterator( a.iterator(), b.iterator(), false, { x, y ->
            [ x, y ]
        } )
    }

    def "collect should return values"() {
        when:
            def result = iter.collect()
        then:
            result == [ [ 1, 3 ], [ null, 4 ] ]
    }

    def "call to next with no hasNext should work"() {
        expect:
            iter.next() == [ 1, 3 ]
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
}