package groovy.stream ;

import groovy.lang.Closure ;
import groovy.lang.GroovyObjectSupport ;

import groovy.stream.iterators.* ;
import groovy.stream.steps.* ;

import java.lang.reflect.Array;

import java.util.ArrayList ;
import java.util.Iterator ;
import java.util.Map ;
import java.util.LinkedHashMap ;
import java.util.List ;

public class Stream<T> implements Iterator<T> {
    private final Iterator<T> iterator ;
    private T                 current ;
    private boolean           exhausted   = false ;
    private boolean           initialised = false ;
    private List<StreamStep>  steps       = new ArrayList<StreamStep>() ;
    private StreamDelegate    delegate    = new StreamDelegate( new LinkedHashMap<String,Object>() ) ;
    private int               unfilteredIndex = -1 ;

    private Stream( Iterator<T> iterator ) {
        this.iterator = iterator ;
    }

    private void loadFirst() {
        if( iterator.hasNext() ) {
            current = iterator.next() ;
        }
        else {
            exhausted = true ;
        }
    }

    @SuppressWarnings("unchecked")
    private void loadNext() {
        outer: while( !exhausted ) {
            if( current == null ) {
                loadFirst() ;
            }
            else {
                if( iterator.hasNext() ) {
                    current = iterator.next() ;
                }
                else {
                    current = null ;
                    exhausted = true ;
                    break ;
                }
            }
            unfilteredIndex++ ;

            for( StreamStep step : steps ) {
                if( step instanceof Delegatable && current instanceof Map ) {
                    ((Delegatable)step).setDelegate( delegate.integrateCurrent( (Map)current ) ) ;
                }
                if( step instanceof MappingStep ) {
                    current = (T)step.execute( current ) ;
                }
                else if( step instanceof FilterStep ) {
                    FilterStep s = (FilterStep)step ;
                    if( s.execute( current ) == Boolean.FALSE ) {
                        continue outer ;
                    }
                }
                else if( step instanceof TerminateStep ) {
                    TerminateStep s = (TerminateStep)step ;
                    if( s.execute( current ) == Boolean.TRUE ) {
                        current = null ;
                        exhausted = true ;
                        break outer ;
                    }
                }
            }
            break ;
        }
    }

    @SuppressWarnings("unchecked")
    private static List cloneList( List l ) {
        return new ArrayList( l ) ;
    }

    @SuppressWarnings("unchecked")
    private static Map cloneMap( Map m ) {
        return new LinkedHashMap( m ) ;
    }

    public boolean isExhausted() {
        return exhausted ;
    }

    public int getStreamIndex() {
        return -1 ;
    }

    public int getUnfilteredIndex() {
        return unfilteredIndex - 1 ;
    }

    private void setDelegate( Map<String,Object> delegate ) {
        this.delegate = new StreamDelegate( delegate ) ;
        delegateSteps() ;
    }

    @Override
    public boolean hasNext() {
        if( !initialised ) {
            loadNext() ;
            initialised = true ;
        }
        return current != null && !exhausted ;
    }

    @Override
    public void remove() { throw new UnsupportedOperationException() ; }

    @SuppressWarnings("unchecked")
    @Override
    public T next() {
        if( !initialised ) {
            hasNext() ;
        }
        T ret ;
        if( current instanceof Map ) {
            ret = (T)Stream.cloneMap( (Map)current ) ;
        }
        else if( current instanceof List ) {
            ret = (T)Stream.cloneList( (List)current ) ;
        }
        else {
            ret = current ;
        }
        loadNext() ;
        return ret ;
    }

    private void delegateSteps() {
        for( StreamStep s : steps ) {
            if( s instanceof Delegatable ) {
                ((Delegatable)s).setDelegate( this.delegate ) ;
            }
        }
        if( iterator instanceof Delegatable ) {
            ((Delegatable)iterator).setDelegate( this.delegate ) ;
        }
    }

    @SuppressWarnings("unchecked")
    public Stream<T> until( final Closure<Boolean> filter )     { return fromStreamWithStep( this, new TerminateStep<T>( filter ) ) ; }
    @SuppressWarnings("unchecked")
    public Stream<T> using( final Map<String,Object> delegate ) { return fromStreamWithDelegate( this, delegate ) ;                   }
    @SuppressWarnings("unchecked")
    public Stream<T> filter( final Closure<Boolean> filter )    { return fromStreamWithStep( this, new FilterStep<T>( filter ) ) ;    }
    @SuppressWarnings("unchecked")
    public <U extends T> Stream<U> map( final Closure<U> map )  { return fromStreamWithStep( this, new MappingStep<U,T>( map ) ) ;    }

    public static     Stream<Map> from( Map<Object,Iterable> map ) { return new Stream<Map>( new MapIterator( map ) ) ;                   }
    public static <T> Stream<T>   from( Iterable<T> iterable )     { return new Stream<T>( iterable.iterator() ) ;                        }
    public static <T> Stream<T>   from( Iterator<T> iterator )     { return new Stream<T>( iterator ) ;                                   }
    public static <T> Stream<T>   from( Closure<T> closure )       { return new Stream<T>( new RepeatingClosureIterator<T>( closure ) ) ; }

    @SuppressWarnings({"unchecked", "varargs"})
    public static <T> Stream<T>         from( T[] array       ) { return new Stream<T>(         primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Byte>      from( byte[] array    ) { return new Stream<Byte>(      primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Character> from( char[] array    ) { return new Stream<Character>( primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Short>     from( short[] array   ) { return new Stream<Short>(     primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Integer>   from( int[] array     ) { return new Stream<Integer>(   primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Long>      from( long[] array    ) { return new Stream<Long>(      primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Float>     from( float[] array   ) { return new Stream<Float>(     primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Double>    from( double[] array  ) { return new Stream<Double>(    primitiveArrayToList( array ).iterator() ) ; }
    @SuppressWarnings("unchecked")
    public static     Stream<Boolean>   from( boolean[] array ) { return new Stream<Boolean>(   primitiveArrayToList( array ).iterator() ) ; }

    @SuppressWarnings("unchecked")
    private static Stream fromStream( Stream other ) {
        Stream ret = new Stream( other.iterator ) ;
        ret.current = other.current ;
        ret.exhausted = other.exhausted ;
        ret.initialised = other.initialised ;
        ret.steps = new ArrayList<StreamStep>( other.steps ) ;
        return ret ;
    }

    @SuppressWarnings("unchecked")
    private static Stream fromStreamWithStep( Stream other, StreamStep step ) {
        Stream ret = fromStream( other ) ;
        ret.steps.add( step ) ;
        ret.delegateSteps() ;
        return ret ;
    }

    @SuppressWarnings("unchecked")
    private static Stream fromStreamWithDelegate( Stream other, Map<String,Object> delegate ) {
        Stream ret = fromStream( other ) ;
        ret.setDelegate( delegate ) ;
        return ret ;
    }

    @SuppressWarnings("unchecked")
    private static List primitiveArrayToList( Object array ) {
        int size = Array.getLength( array ) ;
        List list = new ArrayList( size ) ;
        for( int i = 0 ; i < size ; i++ ) {
            Object item = Array.get( array, i ) ;
            if( item != null && item.getClass().isArray() && item.getClass().getComponentType().isPrimitive() ) {
                item = primitiveArrayToList( item ) ;
            }
            list.add( item ) ;
        }
        return list ;
    }

    protected class StreamDelegate extends GroovyObjectSupport {
        private Map<String,Object> backingMap ;
        private Map<String,Object> currentMap ;

        private StreamDelegate( Map<String,Object> using ) {
            this( using, new LinkedHashMap<String,Object>() ) ;
        }

        private StreamDelegate( Map<String,Object> using, Map<String,Object> current ) {
            this.backingMap = using ;
            this.currentMap = current ;
        }

        public void propertyMissing( String name, Object value ) {
            if( currentMap.keySet().contains( name ) ) {
                currentMap.put( name, value ) ;
            }
            else if( backingMap.keySet().contains( name ) ) {
                backingMap.put( name, value ) ;
            }
        }

        public Object propertyMissing( String name ) {
            if( "streamIndex".equals( name ) )         { return getStreamIndex() ;            }
            if( "unfilteredIndex".equals( name ) )     { return unfilteredIndex ;             }
            if( "exhausted".equals( name ) )           { return isExhausted() ;               }
            if( currentMap.keySet().contains( name ) ) { return currentMap.get( name ) ;      }
            else                                       { return backingMap.get( name ) ;      }      
        }

        @SuppressWarnings("unchecked")
        protected StreamDelegate integrateCurrent( Map currentMap ) {
            return new StreamDelegate( backingMap, currentMap ) ;
        }
    }
}