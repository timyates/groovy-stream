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

import spock.lang.*

class BufferedReaderIteratorTest extends Specification {
    BufferedReader reader

    def setup() {
        String text = '''Line 1
                        |Line 2
                        |Line 3'''.stripMargin()
        reader = new BufferedReader( new StringReader( text ) )
    }

    def "collect should return lines"() {
        when:
            def iter = new BufferedReaderIterator( reader )
            def result = iter.collect()
        then:
            result == [ 'Line 1', 'Line 2', 'Line 3' ]
    }

    def "call to next with no hasNext should work"() {
        when:
            def iter = new BufferedReaderIterator( reader )
        then:
            iter.next() == 'Line 1'
    }

    def "remove should throw UnsupportedOperationException"() {
        when:
            def iter = new BufferedReaderIterator( reader )
            iter.remove()
        then:
            UnsupportedOperationException ex = thrown()
    }

    def "Exhausted iterator should show hasNext = false"() {
        when:
            def iter = new BufferedReaderIterator( reader )
            def result = iter.collect()
        then:
            iter.hasNext() == false
    }

    def "Call to next on Exhausted iterator should throw NoSuchElementException"() {
        when:
            def iter = new BufferedReaderIterator( reader )
            def result = iter.collect()
            iter.next()
        then:
            NoSuchElementException ex = thrown()
    }
}