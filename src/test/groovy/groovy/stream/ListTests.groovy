package groovy.stream

public class ListTests extends spock.lang.Specification {
  def "test index appender"() {
    setup:
    def list = [ 1, 2, 3 ]
    def stream = Stream.from list map { [ it, idx++ ] } using idx:0

    when:
    def result = stream.collect()

    then:
    result == [ [ 1, 0 ], [ 2, 1 ], [ 3, 2 ] ]
  }

  def "test arrays"() {
    setup:
    int[] array = 1..3
    def stream = Stream.from array map { [ it, idx++ ] } using idx:0

    when:
    def result = stream.collect()

    then:
    result == [ [ 1, 0 ], [ 2, 1 ], [ 3, 2 ] ]
  }
}