package groovy.stream.iterators

class MapIteratorTests extends spock.lang.Specification {
  def "test map iterator"() {
    setup:
      def i = new MapIterator( [ tim:1..3, alice:4..6 ] )

    when:
      def result = i.collect()

    then:
      result == [ [tim:1, alice:4], [tim:1, alice:5], [tim:1, alice:6],
                  [tim:2, alice:4], [tim:2, alice:5], [tim:2, alice:6],
                  [tim:3, alice:4], [tim:3, alice:5], [tim:3, alice:6] ]
  }
}