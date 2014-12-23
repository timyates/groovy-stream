package groovy.stream.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator<T> implements Iterator<T> {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException( "EmptyIterator contains no elements" ) ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException( "Remove not supported on EmptyIterator" ) ;
    }
}
