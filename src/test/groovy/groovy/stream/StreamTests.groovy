package groovy.stream

public class StreamTests extends spock.lang.Specification {
  def "test Streaming a List Stream"() {
    setup:
      def instream  = Stream.from 1..3
      def outstream = Stream.from instream
    when:
      def result = outstream.collect()
    then:
      result == [ 1, 2, 3 ]
  }
}
