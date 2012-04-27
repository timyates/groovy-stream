package groovy.stream

public class RangeStreamTests extends spock.lang.Specification {
  def "Simple Range usage"() {
    setup:
    def range  = 1..10
    def stream = Stream.from range

    when:
    def result = stream.collect()

    then:
    result.size()      == range.size()
    stream.exhausted
    stream.streamIndex == range.size() - 1
  }
/*

  def "Limited Range usage"() {
    setup:
    def range  = 1..10
    def limit  = 4..6
    def stream = Stream.from range where { it in limit }

    when:
    def result = stream.collect()

    then:
    result.size()      == limit.size()
    result             == limit
    stream.exhausted
    stream.streamIndex == limit.size() - 1
  }

  def "Transformed Range usage"() {
    setup:
    def range  = 1..3
    def stream = Stream.from range transform { it * 2 }

    when:
    def result = stream.collect()

    then:
    result.size()      == range.size()
    result             == range*.multiply( 2 )
    stream.exhausted
    stream.streamIndex == range.size() - 1
  }

  def "Transformed, Even Range usage"() {
    setup:
    def range    = 1..10
    def expected = [ 4, 8, 12, 16, 20 ]
    def stream   = Stream.from range where { it % 2 == 0 } transform { it * 2 }

    when:
    def result = stream.collect()

    then:
    result             == expected
    stream.exhausted
    stream.streamIndex == expected.size() - 1
  }

  def "Range with local variables"() {
    setup:
    def upper  = 5
    def stream = Stream.from { 1..max } using max:upper

    when:
    def result = stream.collect()

    then:
    result             == 1..upper
    stream.exhausted
    stream.streamIndex == upper - 1
  }

  def "The kitchen sink"() {
    setup:
    def lower    = 3
    def upper    = 5
    def expected = (lower..upper)*.multiply( -1 )
    def stream = Stream.from { 1..max } transform { -it } where { it >= min } using min:lower, max:upper

    when:
    def result = stream.collect()

    then:
    result             == expected
    stream.exhausted
    stream.streamIndex == expected.size() - 1
  }

  def "Ranged index access"() {
    setup:
    def expected = [ 0, 2, 6, 12 ]
    def stream
    stream = Stream.from 1..4 transform { stream.streamIndex * it }

    when:
    def result = stream.collect()

    then:
    result             == expected
    stream.exhausted
    stream.streamIndex == expected.size() - 1
  }
*/
}