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

package groovy.stream.steps ;

import groovy.lang.Closure ;
import groovy.stream.iterators.Delegatable ;

public abstract class AbstractStep<T,U> implements StreamStep<T,U>, Delegatable {
    Closure<T> closure ;
    public AbstractStep( Closure<T> closure ) {
        this.closure = closure ;
    }

    @Override
    public void setDelegate( Object delegate ) {
        closure.setDelegate( delegate ) ;
        closure.setResolveStrategy( Closure.DELEGATE_ONLY ) ;
    }

    @Override
    public T execute( U current ) {
        return closure.call( current ) ;
    }
}