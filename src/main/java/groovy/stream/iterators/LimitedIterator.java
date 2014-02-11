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

import java.util.Iterator ;

public class LimitedIterator<T> implements Iterator<T> {
    private Iterator<T> delegate ;
    private int limit ;

    public LimitedIterator( Iterator<T> delegate, int limit ) {
        this.delegate = delegate ;
        this.limit = limit ;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext() && limit > 0 ;
    }

    @Override
    public T next() {
    	T ret = delegate.next() ;
    	limit-- ;
    	return ret ;
    }

    @Override
    public void remove() {
        delegate.remove() ;
    }
}