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
    private boolean       exhausted ;
    private boolean       initialised ;
    private T             current ;

    public FlatMapIterator( Iterator<T> iterator, Closure<Collection<T>> mapping ) {
        this.mapping = mapping ;
        this.iterator = iterator ;
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
            Collection<T> mapped = mapping.call( next ) ;
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