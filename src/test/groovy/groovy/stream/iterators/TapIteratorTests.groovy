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

class TapIteratorTests extends spock.lang.Specification {
    def "Test NoSuchElementException"() {
        setup:
            def list = []
            def i = new TapIterator( [ 1, 2, null ].iterator(), 2, true, { object, index -> list << [ index, object ] } )

        when:
            def result = i.collect()

        then:
            result == [ 1, 2, null ]
            list   == [ [ 1, 2 ] ]

        when:
            i.next()

        then:
            thrown java.util.NoSuchElementException
  }
}