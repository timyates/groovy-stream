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

public class RangeStreamTests extends spock.lang.Specification {
    def "Simple Range usage"() {
        println "SRU"
        setup:
            def range  = 1..10
            def stream = Stream.from range

        when:
            def result = stream.collect()

        then:
        println "END SRU $stream.unfilteredIndex"
            result.size()          == range.size()
            stream.exhausted
            stream.streamIndex     == range.size()
            stream.unfilteredIndex == range.size() - 1
    }

    def "Limited Range usage"() {
        setup:
            def range  = 1..10
            def limit  = 4..6
            def stream = Stream.from range filter { it in limit }

        when:
            def result = stream.collect()

        then:
            result.size()          == limit.size()
            result                 == limit
            stream.exhausted
            stream.streamIndex     == limit.size()
            stream.unfilteredIndex == range.size() - 1
    }

    def "Transformed Range usage"() {
        setup:
            def range  = 1..3
            def stream = Stream.from range map { it * 2 }

        when:
            def result = stream.collect()

        then:
            result.size()          == range.size()
            result                 == range*.multiply( 2 )
            stream.exhausted
            stream.streamIndex     == range.size()
            stream.unfilteredIndex == range.size() - 1
    }

    def "Transformed, Even Range usage"() {
        setup:
            def range    = 1..10
            def expected = [ 4, 8, 12, 16, 20 ]
            def stream   = Stream.from range filter { it % 2 == 0 } map { it * 2 }

        when:
            def result = stream.collect()

        then:
            result                 == expected
            stream.exhausted
            stream.streamIndex     == expected.size()
            stream.unfilteredIndex == range.size() - 1
    }

    def "Range with local variables"() {
        setup:
            def upper  = 5
            def stream = Stream.from 1..upper

        when:
            def result = stream.collect()

        then:
            result                 == 1..upper
            stream.exhausted
            stream.streamIndex     == upper
            stream.unfilteredIndex == upper - 1
    }

    def "The kitchen sink"() {
        setup:
            def lower    = 3
            def upper    = 5
            def expected = (lower..upper)*.multiply( -1 )
            def stream = Stream.from 1..upper filter { it >= min } map { -it } using min:lower, max:upper

        when:
            def result = stream.collect()

        then:
            result                 == expected
            stream.exhausted
            stream.streamIndex     == expected.size()
            stream.unfilteredIndex == upper - 1
    }

    def "Ranged index access"() {
        setup:
            def expected = [ 0, 2, 6, 12 ]
            def stream
            stream = Stream.from 1..4 map { stream.streamIndex * it }

        when:
            def result = stream.collect()

        then:
            result             == expected
            stream.exhausted
            stream.streamIndex == expected.size()
    }
}