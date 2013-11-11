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
        setup:
            def range  = 1..10
            def stream = Stream.from range
            def index  = 0

        when:
            def result = stream.tapWithIndex { obj, idx -> index = idx }.collect()

        then:
            result.size()     == range.size()
            !stream.hasNext()
            index             == range.size() - 1
    }

    def "Limited Range usage"() {
        setup:
            def range  = 1..10
            def limit  = 4..6
            def index  = 0
            def stream = Stream.from range filter { it in limit } tapWithIndex { obj, idx -> index = idx }

        when:
            def result = stream.collect()

        then:
            result.size()     == limit.size()
            result            == limit
            !stream.hasNext()
            index             == limit.size() - 1
    }

    def "Transformed Range usage"() {
        setup:
            def range  = 1..3
            def index  = 0
            def stream = Stream.from range map { it * 2 } tapWithIndex { obj, idx -> index = idx }

        when:
            def result = stream.collect()

        then:
            result.size()     == range.size()
            result            == range*.multiply( 2 )
            !stream.hasNext()
            index             == range.size() - 1
    }

    def "Transformed, Even Range usage"() {
        setup:
            def range    = 1..10
            def expected = [ 4, 8, 12, 16, 20 ]
            def preIndex = 0
            def index    = 0
            def stream   = Stream.from range tapWithIndex { obj, idx -> preIndex = idx } filter { it % 2 == 0 } map { it * 2 } tapWithIndex { obj, idx -> index = idx }

        when:
            def result = stream.collect()

        then:
            result            == expected
            !stream.hasNext()
            index             == expected.size() - 1
            preIndex          == range.size() - 1
    }

    def "Range with local variables"() {
        setup:
            def upper  = 5
            def index  = 0
            def stream = Stream.from 1..upper tapWithIndex { obj, idx -> index = idx }

        when:
            def result = stream.collect()

        then:
            result            == 1..upper
            !stream.hasNext()
            index             == upper - 1
    }

    def "The kitchen sink"() {
        setup:
            def min      = 3
            def max      = 5
            def index    = 0
            def expected = (min..max)*.multiply( -1 )
            def stream   = Stream.from 1..max filter { it >= min } map { -it } tapWithIndex { obj, idx -> index = idx }

        when:
            def result = stream.collect()

        then:
            result            == expected
            !stream.hasNext()
            index             == expected.size() - 1
    }

    def "Ranged index access"() {
        setup:
            def expected = [ 0, 2, 6, 12 ]
            def stream
            def index = 0
            stream = Stream.from 1..4 tapWithIndex { obj, idx -> index = idx } map { index * it } tapWithIndex { obj, idx -> index = idx }

        when:
            def result = stream.collect()

        then:
            result            == expected
            !stream.hasNext()
            index             == expected.size() - 1
    }
}