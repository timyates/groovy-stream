package groovy.stream ;

import groovy.lang.Closure ;
import java.util.* ;

public class StreamExtension {
    public static <T> Stream toStream( Closure<T> delegate ) {
        return Stream.from( delegate ) ;
    }

    public static <T> Stream toStream( Iterator<T> delegate ) {
        return Stream.from( delegate ) ;
    }

    public static <T> Stream toStream( Iterable<T> delegate ) {
        return Stream.from( delegate ) ;
    }

    public static Stream toStream( Map<Object,Iterable> delegate ) {
        return Stream.from( delegate ) ;
    }
}