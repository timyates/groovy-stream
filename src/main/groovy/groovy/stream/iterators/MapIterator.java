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

package groovy.stream.iterators ;

import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.List ;
import java.util.Map ;
import java.util.LinkedHashMap ;

public class MapIterator implements Iterator<Map> {

    private final Map<Object,Iterable> iterables ;
    private final Map<Object,Iterator> iterators ;
    private final List<Object>         keys ;
    private boolean                    exhausted ;
    private boolean                    initialised ;
    private Map<Object,Object>         current ;

    public MapIterator( Map<Object,Iterable> underlying ) {
        this.iterables = new LinkedHashMap<Object,Iterable>();
        this.iterators = new LinkedHashMap<Object,Iterator>();
        this.keys = new ArrayList<Object>();
        this.exhausted = false;
        this.current = null;
        this.initialised = false;
        for( Map.Entry<Object,Iterable> entry : underlying.entrySet() ) {
            keys.add( entry.getKey() ) ;
            iterables.put( entry.getKey(), entry.getValue() ) ;
            iterators.put( entry.getKey(), entry.getValue().iterator() ) ;
        }
    }

    private void loadFirst() {
        current = new LinkedHashMap<Object,Object>() ;
        for( Object key : keys ) {
            current.put( key, iterators.get( key ).next() ) ;
        }
    }

    private void loadNext() {
        if( current == null ) {
            loadFirst() ;
        }
        else {
            current = new LinkedHashMap<Object,Object>( current ) ;
            for( int i = keys.size() - 1 ; i >= 0 ; i-- ) {
                Object key = keys.get( i ) ;
                if( iterators.get( key ).hasNext() ) {
                    current.put( key, iterators.get( key ).next() ) ;
                    break ;
                }
                else if( i > 0 ) {
                    iterators.put( key, iterables.get( key ).iterator() ) ;
                    current.put( key, iterators.get( key ).next() ) ;
                }
                else {
                    exhausted = true ;
                }
            }
        }
    }

    @Override
    public boolean hasNext() {
        if( !initialised ) {
            loadNext() ;
            initialised = true ;
        }
        return current != null && !exhausted ;
    }

    @Override
    public Map next() {
        Map<Object,Object> ret = new LinkedHashMap<Object,Object>( current ) ;
        loadNext() ;
        return ret ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException() ;
    }
}