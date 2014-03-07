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

import java.util.jar.JarFile
import java.util.zip.ZipFile

class FileTests extends spock.lang.Specification {
    def "Simple Zip test"() {
        setup:
            ZipFile f = new ZipFile( new File( 'src/test/resources/test.zip' ) )
            def stream = Stream.from f map { it.name }
        when:
            def result = stream.collect()
            f.close()
        then:
            result == [ 'a.txt', 'b.txt', 'c.txt' ]
    }

    def "Zip content test"() {
        setup:
            ZipFile f = new ZipFile( new File( 'src/test/resources/test.zip' ) )
            def stream = Stream.from f map { f.getInputStream( it ).text }
        when:
            def result = stream.collect()
            f.close()
        then:
            result == [ 'File A', 'File B', 'And File C' ]
    }

    def "Simple Jar test"() {
        setup:
            JarFile f = new JarFile( new File( 'src/test/resources/test.jar' ) )
            def stream = Stream.from f map { it.name }
        when:
            def result = stream.collect()
            f.close()
        then:
            result == [ 'META-INF/', 'META-INF/MANIFEST.MF', 'a.txt', 'b.txt', 'c.txt' ]
    }

    def "Jar content test"() {
        setup:
            JarFile f = new JarFile( new File( 'src/test/resources/test.jar' ) )
            def stream = Stream.from f filter { !it.directory } map { f.getInputStream( it ).text }
        when:
            def result = stream.collect()
            f.close()
        then:
            result == [ 'Manifest-Version: 1.0\r\nCreated-By: 1.7.0_51 (Oracle Corporation)\r\n\r\n',
                        'File A',
                        'File B',
                        'And File C' ]
    }
}