package groovy.stream ;

public class StreamStopper {
  private static StreamStopper instance = new StreamStopper() ;
  private StreamStopper() {}
  public static StreamStopper getInstance() { return instance ; }
}
