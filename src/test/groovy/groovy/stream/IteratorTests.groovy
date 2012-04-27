package groovy.stream

public class IteratorTests extends spock.lang.Specification {
  def "test list iterator"() {
    setup:
    def iterator = [ 1, 2, 3 ].iterator()
    def stream = Stream.from iterator

    when:
    def result = stream.collect()

    then:
    result == [ 1, 2, 3 ]
  }

  def "Use of the Stream stopper"() {
    setup:
    def x = 0
    def eternal = [ hasNext:{ true }, next:{ x++ } ] as Iterator
    def stream = Stream.from eternal where { it < 5 ?: STOP }

    when:
    def result = stream.collect()

    then:
    result == [ 0, 1, 2, 3, 4 ]
    eternal.next() == 6
    stream.exhausted
    stream.streamIndex == 4
  }
}