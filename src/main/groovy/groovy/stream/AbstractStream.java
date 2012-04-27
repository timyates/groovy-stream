package groovy.stream ;

import groovy.lang.Closure ;
import java.util.List ;
import java.util.Map ;
import java.util.HashMap ;

public abstract class AbstractStream<T,D> implements StreamInterface<T> {
  protected static final Map<String,StreamStopper> stopDelegate = new HashMap<String,StreamStopper>() {{
    put( "HALT", StreamStopper.getInstance() ) ;
  }} ;
  protected int streamIndex = -1 ;
  protected boolean exhausted = false ;
  protected RuntimeException initialisationException = null ;
  Closure<D> definition ;
  Closure condition ;
  Closure<T> transform ;
  Map using ;
  D initial ;
  T current ;
  private boolean initialised ;

  public AbstractStream( Closure<D> definition, Closure condition, Closure<T> transform, Map using ) {
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
    if( initialisationException != null ) {
      throw initialisationException ;
    } 
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