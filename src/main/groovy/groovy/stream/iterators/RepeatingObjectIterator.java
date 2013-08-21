package groovy.stream.iterators ;

import java.util.Iterator ;

public class RepeatingObjectIterator<T> implements Iterator<T> {
    private final T value ;

    public RepeatingObjectIterator( T value ) {
        this.value = value ;
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
        return value ;
    }
}