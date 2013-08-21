package groovy.stream

import spock.lang.Unroll

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

  @Unroll("#name are both an Iterator and an Iterable")
  def "iterator/iterable tests"() {
    expect:
      stream instanceof Iterator
      stream.iterator().is( stream )

    where:
      name << [ 'closure streams', 'map streams', 'range streams' ]
      stream << [
        Stream.from( { x++ } ).using( [ x:1 ] ),
        Stream.from( a:1..3, b:2..4 ).map { a + b },
        Stream.from( 1..4 ).filter { it % 2 == 0 }
      ]
  }
}
