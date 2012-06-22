package groovy.stream ;

import groovy.lang.Closure ;
import java.util.* ;

public class StreamExtension {
  public static Stream toStream( Closure delegate ) {
    return Stream.from( delegate ) ;
  }

  public static Stream toStream( Iterator delegate ) {
    return Stream.from( delegate ) ;
  }

  public static Stream toStream( Iterable delegate ) {
    return Stream.from( delegate ) ;
  }

  public static Stream toStream( LinkedHashMap delegate ) {
    return Stream.from( delegate ) ;
  }

  public static Stream toStream( ArrayList delegate ) {
    return Stream.from( delegate ) ;
  }
}