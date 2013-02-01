/*
 * Copyright 2012 the original author or authors.
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
package groovy.stream ;

import groovy.lang.Closure ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.LinkedHashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Set ;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation ;

/**
 * @author Tim Yates
 */
class StreamImpl<T,D> extends AbstractStream<T,D> {
  private class HeadedIterator<T> implements Iterator {
    T next ;
    Closure<T> nextCall ;

    public HeadedIterator( T initial, Closure<T> definition ) {
      this.next = initial ;
      this.nextCall = definition ;
    }

    public boolean hasNext() {
      return next != null ;
    }

    public T next() { 
      T ret = next ;
      next = nextCall.call() ;
      return ret ;
    }

    public void remove() {}
  }

  private Iterator<T> iterator ;

  protected StreamImpl( Closure<D> definition, Closure condition, Closure<T> transform, LinkedHashMap<String,Object> using ) {
    super( definition, condition, transform, using ) ;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void initialise() {
    initial = this.definition.call() ;

    if( initial instanceof Iterator ) {
      iterator = (Iterator)initial ;
    }
    else if( initial instanceof Iterable ) {
      iterator = ((Iterable)initial).iterator() ;
    }
    else {
      iterator = new HeadedIterator( initial, definition ) ;
    }
  }

  @Override
  public T next() {
    if( !initialised ) {
      hasNext() ;
    }
    T ret = current ;
    loadNext() ;
    this.streamIndex++ ;
    return transform.call( ret ) ;
  }

  @Override
  protected void loadNext() {
    while( !exhausted ) {
      if( current == null && iterator.hasNext() ) {
        current = iterator.next() ;
      }
      else {
        if( iterator.hasNext() ) {
          current = iterator.next() ;
        }
        else {
          exhausted = true ;
        }
      }
      condition.setDelegate( generateMapDelegate( using, stopDelegate ) ) ;
      Object cond = condition.call( current ) ;
      if( cond == StreamStopper.getInstance() ) {
        exhausted = true ;
      }
      else if( DefaultTypeTransformation.castToBoolean( cond ) ) {
        break ;
      }
    }
  }
}