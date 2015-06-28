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

package groovy.stream.iterators;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileIterator implements CloseableIterator<JarEntry> {
    private final JarFile inputFile;
    private final Enumeration<? extends JarEntry> entryEumerator;

    public JarFileIterator(JarFile inputFile) {
        this.inputFile = inputFile;
        entryEumerator = inputFile.entries();
    }

    @Override
    public void close() throws Exception {
        inputFile.close();
    }

    @Override
    public boolean hasNext() {
        return entryEumerator.hasMoreElements();
    }

    @Override
    public JarEntry next() {
        return entryEumerator.nextElement();
    }
}
