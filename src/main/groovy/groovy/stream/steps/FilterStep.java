package groovy.stream.steps ;

import groovy.lang.Closure ;

public class FilterStep<U> extends AbstractStep<Boolean,U> {
    public FilterStep( Closure<Boolean> condition ) {
        super( condition ) ;
    }
}