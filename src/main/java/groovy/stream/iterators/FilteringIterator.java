package groovy.stream.iterators ;

import groovy.lang.Closure ;

import java.util.Iterator ;
import java.util.NoSuchElementException ;

import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation ;

public class FilteringIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator ;
    private final Closure<Boolean> predicate ;
    private T current ;
    private boolean exhausted ;
    private boolean initialised ;

    public FilteringIterator( Iterator<T> iterator, Closure<Boolean> predicate ) {
        this.iterator = iterator ;
        this.predicate = predicate ;
        this.exhausted = false ;
        this.initialised = false ;
    }

    public void remove() {
        iterator.remove() ;
    }

    private void loadNext() {
        while( iterator.hasNext() ) {
            current = iterator.next() ;
            predicate.setDelegate( current ) ;
            if( DefaultTypeTransformation.castToBoolean( predicate.call( current ) ) ) return ;
        }
        exhausted = true ;
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