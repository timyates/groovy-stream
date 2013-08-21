package groovy.stream.steps ;

import groovy.lang.Closure ;

public class MappingStep<T,U> extends AbstractStep<T,U> {
    public MappingStep( Closure<T> condition ) {
        super( condition ) ;
    }
}