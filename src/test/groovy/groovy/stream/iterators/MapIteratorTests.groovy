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

class MapIteratorTests extends spock.lang.Specification {
  def "test map iterator"() {
    setup:
      def i = new MapIterator( [ tim:1..3, alice:4..6 ] )

    when:
      def result = i.collect()

    then:
      result == [ [tim:1, alice:4], [tim:1, alice:5], [tim:1, alice:6],
                  [tim:2, alice:4], [tim:2, alice:5], [tim:2, alice:6],
                  [tim:3, alice:4], [tim:3, alice:5], [tim:3, alice:6] ]
  }

  def "Test NoSuchElementException"() {
    setup:
      def i = new MapIterator( [ tim:1..3, alice:4..6 ] )

    when:
      def result = i.collect()

    then:
      result == [ [tim:1, alice:4], [tim:1, alice:5], [tim:1, alice:6],
                  [tim:2, alice:4], [tim:2, alice:5], [tim:2, alice:6],
                  [tim:3, alice:4], [tim:3, alice:5], [tim:3, alice:6] ]
    when:
      i.next()

    then:
      thrown java.util.NoSuchElementException
  }
}