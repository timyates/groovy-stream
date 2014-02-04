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

class ReaderTests extends spock.lang.Specification {
    def "Quick test"() {
        when:
            def reader = new StringReader( '''Line 1
                                             |Line 2
                                             |Line 3'''.stripMargin() )
            def stream = Stream.from new BufferedReader( reader )

        then:
            stream.collect() == ['Line 1', 'Line 2', 'Line 3' ]
    }

    def "Joining test"() {
        when:
            def reader = new StringReader( '''ABC
                                             |DEF
                                             |GHI'''.stripMargin() )
            def stream = Stream.from new BufferedReader( reader )

        then:
            stream.flatMap { e -> e.toList() }
                  .collate( 4 )
                  .map { it.join() }
                  .collect() == [ 'ABCD', 'EFGH', 'I' ]
    }

    def "Skipping"() {
        when:
            def reader = new StringReader( '''ABC
                                             |DEF
                                             |GHI'''.stripMargin() )
            def stream = Stream.from new BufferedReader( reader )

        then:
            stream.skip( 1 )
                  .flatMap { e -> e.toList() }
                  .collate( 4 )
                  .map { it.join() }
                  .collect() == [ 'DEFG', 'HI' ]
    }

    def "Skipping after flatmap"() {
        when:
            def reader = new StringReader( '''ABC
                                             |DEF
                                             |GHI'''.stripMargin() )
            def stream = Stream.from new BufferedReader( reader )

        then:
            stream.flatMap { e -> e.toList() }
                  .skip( 2 )
                  .collate( 4 )
                  .map { it.join() }
                  .collect() == [ 'CDEF', 'GHI' ]
    }
}