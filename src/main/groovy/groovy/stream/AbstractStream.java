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
import java.util.List ;
import java.util.Map ;
import java.util.HashMap ;

/**
 * Base class for all Stream implementations.
 */
abstract class AbstractStream<T,D> implements StreamInterface<T> {
  protected static final Map<String,StreamStopper> stopDelegate = new HashMap<String,StreamStopper>() {{
    put( "STOP", StreamStopper.getInstance() ) ;
  }} ;
  protected int streamIndex = -1 ;
  protected boolean exhausted = false ;
  Closure<D> definition ;
  Closure condition ;
  Closure<T> transform ;
  Map<String,Object> using ;
  D initial ;
  T current ;
  private boolean initialised ;

  protected AbstractStream( Closure<D> definition, Closure condition, Closure<T> transform, Map<String,Object> using ) {
    this.using = using ;

    this.definition = definition ;
    this.definition.setDelegate( this.using ) ;
    this.definition.setResolveStrategy( Closure.DELEGATE_FIRST ) ;

    this.condition = condition ;
    this.condition.setDelegate( this.using ) ;
    this.condition.setResolveStrategy( Closure.DELEGATE_FIRST ) ;

    this.transform = transform ;
    this.transform.setDelegate( this.using ) ;
    this.transform.setResolveStrategy( Closure.DELEGATE_FIRST ) ;
  }

  protected Closure<D> getDefinition() { return definition ; }
  protected Closure getCondition() { return condition ; }
  protected Closure<T> getTransform() { return transform ; }
  protected Map getUsing() { return using ; }

  protected abstract void initialise() ;

  public int getStreamIndex() {
    return streamIndex ;
  }

  public boolean isExhausted() {
    return exhausted ;
  }

  protected abstract void loadNext() ;

  @SuppressWarnings("unchecked")
  protected Map generateMapDelegate( Map... subMaps ) {
    Map ret = new HashMap() ;
    for( Map m : subMaps ) {
      ret.putAll( m ) ;
    }
    return ret ;
  }

  @Override
  public boolean hasNext() {
    if( !initialised ) {
      initialise() ;
      loadNext() ;
      initialised = true ;
    }
    return current != null && !exhausted ;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException( "Cannot call remove() on a Stream" ) ;
  }
}