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

public class StreamImpl<T,D> extends AbstractStream<T,D> {
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

  public StreamImpl( Closure<D> definition, Closure condition, Closure<T> transform, LinkedHashMap<String,Object> using ) {
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
    T ret = current ;
    loadNext() ;
    this.streamIndex++ ;
    return transform.call( ret ) ;
  }

  @Override
  protected void loadNext() {
    while( !exhausted ) {
      if( current == null ) {
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
      if( DefaultTypeTransformation.castToBoolean( condition.call( current ) ) ) break ;
    }
  }
}