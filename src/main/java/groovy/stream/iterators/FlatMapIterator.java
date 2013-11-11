package groovy.stream ;

import groovy.lang.Closure ;
import java.util.Collection ;
import java.util.Iterator ;
import java.util.LinkedList ;
import java.util.Queue ;
import java.util.NoSuchElementException ;

public class FlatMapIterator<T,U extends Collection<T>> implements Iterator<T> {
    Closure<Collection<T>> mapping ;
    private Queue<T>      pushback = new LinkedList<T>() ;
    private Iterator<T>   iterator ;
    private int           index = 0 ;
    private boolean       withIndex ;
    private boolean       exhausted ;
    private boolean       initialised ;
    private T             current ;

    public FlatMapIterator( Iterator<T> iterator, Closure<Collection<T>> mapping, boolean withIndex ) {
        this.mapping = mapping ;
        this.iterator = iterator ;
        this.withIndex = withIndex ;
        this.exhausted = false ;
        this.initialised = false ;
    }

    public void remove() {
        iterator.remove() ;
    }

    private void loadNext() {
        if( pushback.isEmpty() && iterator.hasNext() ) {
            T next = iterator.next() ;
            mapping.setDelegate( next ) ;
            Collection<T> mapped = withIndex ? mapping.call( next, index ) : mapping.call( next ) ;
            index++ ;
            for( T element : mapped ) {
                pushback.offer( element ) ;
            }
        }
        if( !pushback.isEmpty() ) {
            current = pushback.poll() ;
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
            throw new NoSuchElementException( "FlatMapIterator has been exhausted and contains no more elements" ) ;
        }
        T ret = current ;
        loadNext() ;
        return ret ;
    }
}