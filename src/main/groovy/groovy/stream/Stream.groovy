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

  public static Stream from( Map a ) {
    new Stream( type:StreamType.MAP, wrapped:new MapStream( { a }, { true }, { it }, [:] ) )
  }

  public static Stream from( a ) {
    if( a instanceof Closure ) {
      new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( a, { true }, { it }, [:] ) )
    }
    else {
      new Stream( type:StreamType.OTHER, wrapped:new StreamImpl( { a }, { true }, { it }, [:] ) )
    }
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
}