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

import spock.lang.Specification

class RepeatingIteratorTests extends Specification {
    def "Check empty repeat (no repeat)"() {
        setup:
            def iter = new RepeatingIterator([ 1, 2, null ].iterator(), 0)
        when:
            def result = iter.collect()
        then:
            result == []
    }

    def "Check single repeat"() {
        setup:
            def iter = new RepeatingIterator([ 1, 2, null ].iterator(), 1)
        when:
            def result = iter.collect()
        then:
            result == [1, 2, null]
    }

    def "Check double repeat"() {
        setup:
            def iter = new RepeatingIterator([ 1, 2, null ].iterator(), 2)
        when:
            def result = iter.collect()
        then:
            result == [1, 2, null, 1, 2, null]
    }

    def "Check endless repeat"() {
        setup:
            def iter = new RepeatingIterator([ 1, 2, null ].iterator())
        when:
            // How to check infinity? Let's go with 20 as a placeholder for infinity ;)
            def result = iter.take(20).collect()
        then:
            result == [1, 2, null, 1, 2, null, 1, 2, null, 1, 2, null, 1, 2, null, 1, 2, null, 1, 2]
    }
}