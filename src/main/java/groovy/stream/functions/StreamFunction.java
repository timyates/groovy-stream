package groovy.stream.functions ;

public interface StreamFunction<T,S> {
    S call( T value ) ;
}