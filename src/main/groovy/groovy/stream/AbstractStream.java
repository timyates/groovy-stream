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
import groovy.lang.GroovyObjectSupport ;
import java.util.List ;
import java.util.Map ;
import java.util.HashMap ;

/**
 * Base class for all Stream implementations.
 *
 * @author Tim Yates
 */
abstract class AbstractStream<T,D> implements StreamInterface<T> {
  protected class StreamDelegate extends GroovyObjectSupport {
    private Map<String,Object> backingMap ;
    private Map<String,Object> currentMap ;

    private StreamDelegate( Map<String,Object> using ) {
      this( using, new HashMap<String,Object>() ) ;
    }

    private StreamDelegate( Map<String,Object> using, Map<String,Object> current ) {
      this.backingMap = using ;
      this.currentMap = current ;
    }

    public void propertyMissing( String name, Object value ) {
      System.out.println( "PM: '" + name + "' " + value ) ;
      if( currentMap.keySet().contains( name ) ) {
        currentMap.put( name, value ) ;
      }
      else if( backingMap.keySet().contains( name ) ) {
        backingMap.put( name, value ) ;
      }
    }

    public Object propertyMissing( String name ) {
      if( "streamIndex".equals( name ) )         { return getStreamIndex() ;            }
      if( "unfilteredIndex".equals( name ) )     { return getUnfilteredIndex() ;        }
      if( "exhausted".equals( name ) )           { return isExhausted() ;               }
      if( "STOP".equals( name ) )                { return StreamStopper.getInstance() ; }
      if( currentMap.keySet().contains( name ) ) { return currentMap.get( name ) ;      }
      else                                       { return backingMap.get( name ) ;      }      
    }

    @SuppressWarnings("unchecked")
    protected StreamDelegate integrateCurrent( Map currentMap ) {
      return new StreamDelegate( backingMap, currentMap ) ;
    }
  }

  final StreamDelegate delegate ;
  protected int streamIndex = -1 ;
  protected int unfilteredIndex = -1 ;
  protected boolean exhausted = false ;
  Closure<D> definition ;
  Closure condition ;
  Closure<T> transform ;
  Map<String,Object> using ;
  Closure<Boolean> until ;
  D initial ;
  T current ;
  protected boolean initialised ;

  protected AbstractStream( Closure<D> definition, Closure condition, Closure<T> transform, Map<String,Object> using, Closure<Boolean> until ) {
    this.using = using ;

    this.delegate = new StreamDelegate( this.using ) ;

    this.definition = definition ;
    this.definition.setDelegate( this.delegate ) ;
    this.definition.setResolveStrategy( Closure.DELEGATE_ONLY ) ;

    this.condition = condition ;
    this.condition.setDelegate( this.delegate ) ;
    this.condition.setResolveStrategy( Closure.DELEGATE_ONLY ) ;

    this.transform = transform ;
    this.transform.setDelegate( this.delegate ) ;
    this.transform.setResolveStrategy( Closure.DELEGATE_ONLY ) ;

    this.until = until ;
    this.until.setDelegate( this.delegate ) ;
    this.until.setResolveStrategy( Closure.DELEGATE_ONLY ) ;
  }

  protected Closure<D> getDefinition() { return definition ; }
  protected Closure getCondition() { return condition ; }
  protected Closure<T> getTransform() { return transform ; }
  protected Map getUsing() { return using ; }
  protected Closure<Boolean> getUntil() { return until ; }

  protected abstract void initialise() ;

  public int getStreamIndex() {
    return streamIndex ;
  }

  public int getUnfilteredIndex() {
    return unfilteredIndex ;
  }

  public boolean isExhausted() {
    return exhausted ;
  }

  protected abstract void loadNext() ;

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