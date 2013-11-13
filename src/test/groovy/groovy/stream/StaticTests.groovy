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

public class StaticTests extends spock.lang.Specification {

    @groovy.transform.CompileStatic
    private Collection<Integer> generate() {
        Stream.from( [ x:1..3, y:1..3 ] )
              .map { Map<String,Integer> it -> it.x + it.y }
              .collect()
    }

    def "Quick test"() {
        when:
            def result = generate()

        then:
            result == [ 2, 3, 4, 3, 4, 5, 4, 5, 6 ]
    }

    /*
    @groovy.transform.CompileStatic
    private Collection<Integer> generateExtension() {
        [ 1, 2, 3 ].toStream()
                   .collect()
    }
    */

    @spock.lang.Ignore("Extensions cannot yet be statically checked")
    def "Static extension"() {
        when:
            def result = generateExtension()

        then:
            result == [ 1, 2, 3 ]
    }
}
