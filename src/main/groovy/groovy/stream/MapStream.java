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

public class MapStream<T,D extends LinkedHashMap<String,Iterable>> extends AbstractStream<T,D> {
  private Map<String,Iterator> iterators ;
  private List<String> keys ;

  public MapStream( Closure<D> definition, Closure<Boolean> condition, Closure<T> transform, LinkedHashMap<String,Object> using ) {
    super( definition, condition, transform, using ) ;
  }

  protected void initialise() {
    try { 
      initial = this.definition.call() ;

      iterators = new HashMap<String,Iterator>() ;

      for( Map.Entry<String,Iterable> e : initial.entrySet() ) {
        iterators.put( e.getKey(), e.getValue().iterator() ) ;
      }
      keys = new ArrayList<String>( initial.keySet() ) ;
    }
    catch( Exception e ) {
      initialisationException = new RuntimeException( e.getMessage() ) ;
      exhausted = true ;
    }
  }

  @SuppressWarnings("unchecked")
  private T cloneMap( Map m ) {
    return (T)new LinkedHashMap( m ) ;
  }

  @SuppressWarnings("unchecked")
  private Map generateMapDelegate( Map... subMaps ) {
    Map ret = new HashMap() ;
    for( Map m : subMaps ) {
      ret.putAll( m ) ;
    }
    return ret ;
  }

  @Override
  public T next() {
    if( initialisationException != null ) {
      throw initialisationException ;
    }
    T ret = cloneMap( (Map)current ) ;
    transform.setDelegate( generateMapDelegate( using, (Map)current ) ) ;
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

  @SuppressWarnings("unchecked")
  protected void loadNext() {
    while( !exhausted ) {
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
      condition.setDelegate( current ) ;
      if( DefaultTypeTransformation.castToBoolean( condition.call() ) ) break ;
    }
  }
}