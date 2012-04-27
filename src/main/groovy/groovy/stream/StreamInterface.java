package groovy.stream ;

import java.util.Iterator ;

public interface StreamInterface<T> extends Iterator<T> {
  public boolean isExhausted() ;
  public int getStreamIndex() ;
}