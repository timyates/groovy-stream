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

public class ReadmeTests extends spock.lang.Specification {
    def "first example"() {
        setup:
            def s = Stream.from x:1..2, y:1..4 filter { x + y == 4 }

        when:
            def result = s.collect()

        then:
            result == [ [x:1, y:3], [x:2, y:2] ]
    }
}
