package groovy.stream

public class MapTests extends spock.lang.Specification {
  def "Simple Map"() {
    setup:
      def stream = Stream.from x:1..2, y:1..2

    when:
      def result = stream.collect()

    then:
      result == [[x:1, y:1], [x:1, y:2], [x:2, y:1], [x:2, y:2]]
  }

  def "Map with limits"() {
    setup:
      def stream = Stream.from x:1..2, y:1..2 filter { x == y }

    when:
      def result = stream.collect()

    then:
      result == [ [ x:1, y:1 ], [ x:2, y:2 ] ]
  }

  def "Map with transformation"() {
    setup:
      def stream = Stream.from x:1..2, y:1..2 map { x + y }

    when:
      def result = stream.collect()

    then:
      result == [ 2, 3, 3, 4 ]
  }

  def "Map with transformation and limits"() {
    setup:
      def stream = Stream.from x:1..3, y:1..3 filter { x == y } map { x + y }

    when:
      def result = stream.collect()

    then:
      result == [ 2, 4, 6 ]
  }

  def "Issue #3 - Where with static bindings..."() {
    setup:
      def x = 0
      // Map is now passed in as parameter to filter closure so we can get at it
      // { x == 1 } will always resolve to false as x is 0 in static binding
      def stream = Stream.from x:1..3, y:1..3 filter { it.x == 1 } 

    when:
      def result = stream.collect()

    then:
      result == [ [ x:1,y:1 ], [x:1,y:2], [x:1,y:3 ] ]
  }
}
