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
    def stream = Stream.from eternal filter { it < 5 ?: STOP }

    when:
    def result = stream.collect()

    then:
    result == [ 0, 1, 2, 3, 4 ]
    eternal.next() == 6
    stream.exhausted
    stream.streamIndex == 4
  }

  def "Use of the Stream stopper with large ranges"() {
    setup:
    // Ranges as of Groovy 1.8.6 can't be > Integer.MAX_VALUE
    // in length else iterator() fails
    def eternal = (1..2147483647).iterator()
    def stream = Stream.from eternal filter { it < 5 ?: STOP }

    when:
    def result = stream.collect()

    then:
    result == [ 1, 2, 3, 4 ]
    eternal.next() == 6
    stream.exhausted
    stream.streamIndex == 3
  }

  def "Stream of randoms"() {
    setup:
      def rnd = new Random( 10 )
      def rndIterator = [ hasNext:{ true }, next:{ rnd.nextDouble() } ] as Iterator
      def stream = Stream.from rndIterator filter { it < 0.5 } map { ( it * 100 ) as Integer }

    when:
      def result = stream.take( 2 ).collect()

    then:
      result == [ 25, 5 ]
  }

  def "Github issue #11: Just calling next() causes NPE"() {
    setup:
      def iter = [ hasNext:{ true }, next:{ 1 } ] as Iterator
      def stream = Stream.from iter

    when:
      def result = stream.next()

    then:
      result == 1
  }
}