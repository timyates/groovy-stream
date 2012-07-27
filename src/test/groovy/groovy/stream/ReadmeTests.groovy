package groovy.stream

public class ReadmeTests extends spock.lang.Specification {
  def "first example"() {
    setup:
    def s = Stream.from x:1..2, y:1..4 filter { x + y == 4 }

    when:
    def result = s.collect()

    then:
    result == [ [x:1, y:3], [x:2, y:2] ]
  }

  def "second example"() {
    setup:
    def s = Stream.from 1..10 map { it + x++ } filter { it % 2 } using x:0

    when:
    def result = s.collect()

    then:
    result == [ 1, 4, 7, 10, 13 ]
  }
}
