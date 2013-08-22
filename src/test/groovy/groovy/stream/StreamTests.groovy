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

import spock.lang.Unroll

public class StreamTests extends spock.lang.Specification {
    def "test Streaming a List Stream"() {
        setup:
            def instream  = Stream.from 1..3
            def outstream = Stream.from instream
        when:
            def result = outstream.collect()
        then:
            result == [ 1, 2, 3 ]
    }

    @Unroll("#name are both an Iterator and an Iterable")
    def "iterator/iterable tests"() {
        expect:
            stream instanceof Iterator
            stream.iterator().is( stream )

        where:
            name << [ 'closure streams', 'map streams', 'range streams' ]
            stream << [
                Stream.from( { x++ } ).using( [ x:1 ] ),
                Stream.from( a:1..3, b:2..4 ).map { a + b },
                Stream.from( 1..4 ).filter { it % 2 == 0 }
            ]
    }
}
