package groovy.stream

public class ObjectTests extends spock.lang.Specification {
  def "test obj appender"() {
    setup:
      def stream = Stream.from { 1 }

    when:
      def result = stream.take( 4 ).collect()

    then:
      result == [ 1, 1, 1, 1 ]
  }
}
