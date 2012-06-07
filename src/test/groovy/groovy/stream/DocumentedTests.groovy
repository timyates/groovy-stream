package groovy.stream

public class DocumentedTests extends spock.lang.Specification {
  def "test 1"() {
    setup:
      def stream = Stream.from 1..3
    when:
      def result = stream.collect()
    then:
      result == [ 1, 2, 3 ]
  }

  def "test 2"() {
    setup:
      def stream = Stream.from x:1..3, y:'a'..'b'
    when:
      def result = stream.collect()
    then:
      result == [ [x:1,y:'a'], [x:1,y:'b'], [x:2,y:'a'], [x:2,y:'b'], [x:3,y:'a'], [x:3,y:'b'] ]
  }

  def "test 3"() {
    setup:
      def stream = Stream.from x:1..3, y:'a'..'b' where { x % 2 == 0 }
    when:
      def result = stream.collect()
    then:
      result == [ [x:2,y:'a'], [x:2,y:'b'] ]
  }

  def "test 4"() {
    setup:
      def stream = Stream.from x:1..3, y:'a'..'b' transform { [ x:x*2, y:"letter $y" ] }
    when:
      def result = stream.collect()
    then:
      result == [ [x:2,y:'letter a'], [x:2,y:'letter b'],
                  [x:4,y:'letter a'], [x:4,y:'letter b'],
                  [x:6,y:'letter a'], [x:6,y:'letter b'] ]
  }

  def "test 5"() {
    setup:
      def stream = Stream.from 'a'..'c' transform { [ idx++, it ] } using idx:0
    when:
      def result = stream.collect()
    then:
      result == [ [0,'a'], [1,'b'], [2,'c'] ]
  }
}