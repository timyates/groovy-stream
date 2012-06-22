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

  public static Stream toStream( Map delegate ) {
    return Stream.from( delegate ) ;
  }
}