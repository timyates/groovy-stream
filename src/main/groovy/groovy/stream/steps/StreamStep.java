package groovy.stream.steps ;

public interface StreamStep<T,U> {
    T execute( U current ) ;
}