package groovy.stream.steps ;

import groovy.lang.Closure ;

public class TerminateStep<U> extends AbstractStep<Boolean,U> {
    public TerminateStep( Closure<Boolean> condition ) {
        super( condition ) ;
    }
}