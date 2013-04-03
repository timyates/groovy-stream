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
class MapStream<T,D extends LinkedHashMap<String,Iterable>> extends AbstractStream<T,D> {
  private Map<String,Iterator> iterators ;
  private List<String> keys ;

  protected MapStream( Closure<D> definition, Closure condition, Closure<T> transform, LinkedHashMap<String,Object> using, Closure<Boolean> until ) {
    super( definition, condition, transform, using, until ) ;
  }

  @Override
  protected void initialise() {
    initial = this.definition.call() ;

    iterators = new HashMap<String,Iterator>() ;

    for( Map.Entry<String,Iterable> e : initial.entrySet() ) {
      iterators.put( e.getKey(), e.getValue().iterator() ) ;
    }
    keys = new ArrayList<String>( initial.keySet() ) ;
  }

  @SuppressWarnings("unchecked")
  private T cloneMap( Map m ) {
    return (T)new LinkedHashMap( m ) ;
  }

  @Override
  public T next() {
    T ret = cloneMap( (Map)current ) ;
    transform.setDelegate( delegate.integrateCurrent( (Map)ret ) ) ;
    loadNext() ;
    this.streamIndex++ ;
    return transform.call( ret ) ;
  }

  @SuppressWarnings("unchecked")
  private T getFirst() {
    Map newMap = new LinkedHashMap<String,Object>() ;
    for( String key : keys ) {
      newMap.put( key, iterators.get( key ).next() ) ;
    }
    return (T)newMap ;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void loadNext() {
    while( !exhausted ) {
      this.unfilteredIndex++ ;
      if( current == null ) {
        current = getFirst() ;
      }
      else {
        for( int i = keys.size() - 1 ; i >= 0 ; i-- ) {
          String key = keys.get( i ) ;
          if( iterators.get( key ).hasNext() ) {
            ((Map)current).put( key, iterators.get( key ).next() ) ;
            break ;
          }
          else if( i > 0 ) {
            iterators.put( key, initial.get( key ).iterator() ) ;
            ((Map)current).put( key, iterators.get( key ).next() ) ;
          }
          else {
            exhausted = true ;
          }
        }
      }
      if( until.call( current ) ) {
        exhausted = true ;
        break ;
      }
      condition.setDelegate( delegate.integrateCurrent( (Map)current ) ) ;
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