package groovy.stream

public class Stream<T> implements StreamInterface<T> {
  StreamInterface wrapped
  private static enum StreamType { MAP, OTHER }
  private StreamType type
  public boolean hasNext()        { wrapped.hasNext() }
  public T       next()           { wrapped.next() }
  public void    remove()         { wrapped.remove() }
  public boolean isExhausted()    { wrapped.exhausted }
  public int     getStreamIndex() { wrapped.streamIndex }

  public Stream from( Closure a ) {
    new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( a, { true }, { it }, [:] ) )
  }

  public static Stream from( Map a ) {
    new Stream( type:StreamType.MAP, wrapped:new MapStream( { a }, { true }, { it }, [:] ) )
  }

  public static Stream from( a ) {
    println "WOO $a"
    new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( { a }, { true }, { it }, [:] ) )
  }

  public Stream where( where ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, where, wrapped.transform, wrapped.using ) :
              new StreamImpl( wrapped.definition, where, wrapped.transform, wrapped.using )
    this
  }
  
  public Stream transform( transform ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, wrapped.condition, transform, wrapped.using ) :
              new StreamImpl( wrapped.definition, wrapped.condition, transform, wrapped.using )
    this
  }
  
  public Stream using( Map using ) {
    wrapped = type == StreamType.MAP ?
              new MapStream( wrapped.definition, wrapped.condition, wrapped.transform, using ) :
              new StreamImpl( wrapped.definition, wrapped.condition, wrapped.transform, using )
    this
  }

/*
  class HeadedIterator implements Iterator {
    def next
    Closure nextCall
    
    boolean hasNext() { next != null }
    Object next() { 
      def ret = next
      next = nextCall.call()
      ret
    }
    void remove() {}
  }

  private Closure definition
  private Closure condition
  private Closure transform
  private Throwable exception
  private boolean exhausted
  private Closure loadNext
  private int streamIndex = -1
  private Map using 
  private Map<String,Iterator> iterators
  private boolean initialized = false
  private List<String> keys
  private def initial
  private def current = null
  
  private Iterator toIterator( initial ) {
    if( initial instanceof Iterator ) {
      initial
    }
    else if( initial instanceof Iterable ) {
      initial.iterator()
    }
    else {
      new HeadedIterator( next:initial, nextCall:definition )
    }
  }
  
  public Stream( Closure definition, Closure condition={ true }, Closure transform={ it }, Map using=[:] ) {
    this.definition = definition
    this.definition.delegate = using
    this.definition.resolveStrategy = Closure.DELEGATE_FIRST
    this.using = using
    this.condition = condition
    this.condition.delegate = this.using
    this.condition.resolveStrategy = Closure.DELEGATE_FIRST
    this.transform = transform
    this.transform.delegate = this.using
    this.transform.resolveStrategy = Closure.DELEGATE_FIRST
    try { 
      initial = definition.call()
      if( initial instanceof Map ) {
        iterators = initial.collectEntries { 
          if( it.value instanceof Iterator ) {
            throw new IllegalArgumentException( 'Map params may not be Iterators' )
          }
          [ (it.key):it.value.iterator() ]
        }
        keys = initial.keySet() as List
        this.loadNext = this.&mapNext
      }
      else {
        iterators = [ iterable:toIterator( initial ) ]
        this.loadNext = this.&iterableNext
      }
    }
    catch( e ) {
      exception = e
      exhausted = true
    }
  }

  private void iterableNext() {
    while( !exhausted ) {
      if( current == null ) {
        current = iterators.iterable.next()
      }
      else {
        if( iterators.iterable.hasNext() ) {
          current = iterators.iterable.next()
        }
        else {
          exhausted = true
        }
      }
      if( condition.call( current ) ) break
    }
  }

  private void mapNext() {
    while( !exhausted ) {
      if( current == null ) {
        current = keys.collectEntries { [ (it):iterators[ it ].next() ] }
      }
      else {
        for( i in 0..<keys.size() ) {
          if( iterators[ keys[ i ] ].hasNext() ) {
            current[ keys[ i ] ] = iterators[ keys[ i ] ].next() 
            break
          }
          else if( i < keys.size() - 1 ) {
            iterators[ keys[ i ] ] = initial[ keys[ i ] ].iterator()
            current[ keys[ i ] ] = iterators[ keys[ i ] ].next()
          }
          else {
            exhausted = true
          }
        }
      }
      condition.delegate = current
      if( condition.call() ) break
    }
  }
  
  public boolean hasNext() {
    if( exception ) throw exception
    if( !initialized ) {
      loadNext()
      initialized = true
    }
    current != null && !exhausted
  }

  public Object next() {
    if( exception ) throw exception
    def ret
    if( current instanceof Map ) {
      ret = current.clone()
      transform.delegate = using << current
    }
    else {
      ret = current
    }
    loadNext()
    this.streamIndex++
    transform.call( ret )
  }

  public void remove() {}
  public boolean isExhausted()        { exhausted }
  public int getStreamIndex()         { streamIndex }
  public void setStreamIndex( int i ) { throw new RuntimeException( 'Cannot set read-only property streamIndex' ) }
  
  static Stream from( Closure a ) {
    new Stream( a )
  }

  static Stream from( a ) {
    new Stream( { a } )
  }
  
  public Stream where( b ) {
    new Stream( definition, b, transform, using )
  }
  
  public Stream transform( c ) {
    new Stream( definition, condition, c, using )
  }
  
  public Stream using( Map d ) {
    new Stream( definition, condition, transform, d )
  }
*/
}