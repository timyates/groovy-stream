package groovy.stream.iterators ;

import groovy.lang.Closure ;

import java.util.Iterator ;
import java.util.NoSuchElementException ;

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation ;

public class UntilIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator ;
    private final Closure<Boolean> predicate ;

    private T       current ;
    private int     index = 0 ;
    private boolean withIndex ;
    private boolean exhausted ;
    private boolean initialised ;

    public UntilIterator( Iterator<T> iterator, Closure<Boolean> predicate, boolean withIndex ) {
        this.iterator = iterator ;
        this.predicate = predicate ;
        this.withIndex = withIndex ;
        this.exhausted = false ;
        this.initialised = false ;
    }

    public void remove() {
        iterator.remove() ;
    }

    private void loadNext() {
        if( iterator.hasNext() ) {
            current = iterator.next() ;
            predicate.setDelegate( current ) ;
            Boolean check = withIndex ?
                                DefaultTypeTransformation.castToBoolean( predicate.call( current, index ) ) :
                                DefaultTypeTransformation.castToBoolean( predicate.call( current ) ) ;
            index++ ;
            if( check ) {
                exhausted = true ;
            }
        }
        else {
            exhausted = true ;
        }
    }

    public boolean hasNext() {
        if( !initialised ) {
            loadNext() ;
            initialised = true ;
        }
        return !exhausted ;
    }

    public T next() {
        if( !initialised ) {
            hasNext() ;
        }
        if( exhausted ) {
            throw new NoSuchElementException( "FilterIterator has been exhausted and contains no more elements" ) ;
        }
        T ret = current ;
        loadNext() ;
        return ret ;
    }
}