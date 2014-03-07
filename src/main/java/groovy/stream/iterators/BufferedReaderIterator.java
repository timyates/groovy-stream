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

package groovy.stream.iterators ;

import java.io.BufferedReader ;
import java.io.IOException ;
import java.util.Iterator ;
import java.util.NoSuchElementException ;

public class BufferedReaderIterator implements Iterator<String> {
    private String current ;
    private final BufferedReader reader ;
    private boolean  exhausted ;
    private boolean  loaded ;

    public BufferedReaderIterator( BufferedReader r ) {
        this.reader = r ;
        this.exhausted = false;
        this.current = null;
        this.loaded = false;
    }

    private void loadNext() {
        try {
            current = reader.readLine() ;
            if( current == null ) {
                exhausted = true ;
            }
        }
        catch( IOException ex ) {
            exhausted = true ;
        }
    }

    @Override
    public boolean hasNext() {
        if( !loaded ) {
            loadNext() ;
            loaded = true ;
        }
        return !exhausted ;
    }

    @Override
    public String next() {
        hasNext() ;
        if( exhausted ) {
            throw new NoSuchElementException( "BufferedReaderIterator has been exhausted and contains no more elements" ) ;
        }
        String ret = current ;
        loaded = false ;
        return ret ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException( "Remove not supported on BufferedReaderIterator" ) ;
    }
}