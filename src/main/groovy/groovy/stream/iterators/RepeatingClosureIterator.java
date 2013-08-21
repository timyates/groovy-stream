package groovy.stream.iterators ;

import groovy.lang.Closure ;
import java.util.Iterator ;

public class RepeatingClosureIterator<T> implements Iterator<T>, Delegatable {
    Closure<T> value ;

    public RepeatingClosureIterator( Closure<T> value ) {
        this.value = value ;
    }

    @Override
    public void setDelegate( Object delegate ) {
        value.setDelegate( delegate ) ;
        value.setResolveStrategy( Closure.DELEGATE_ONLY ) ;
    }

    @Override
    public boolean hasNext() {
        return true ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException() ;
    }

    @Override
    public T next() {
        return value.call() ;
    }
}