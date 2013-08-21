package groovy.stream.steps ;

import groovy.lang.Closure ;
import groovy.stream.iterators.Delegatable ;

public abstract class AbstractStep<T,U> implements StreamStep<T,U>, Delegatable {
    Closure<T> closure ;
    public AbstractStep( Closure<T> closure ) {
        this.closure = closure ;
    }

    @Override
    public void setDelegate( Object delegate ) {
        closure.setDelegate( delegate ) ;
        closure.setResolveStrategy( Closure.DELEGATE_ONLY ) ;
    }

    @Override
    public T execute( U current ) {
        System.out.println( "Current = " + current ) ;
        return closure.call( current ) ;
    }
}