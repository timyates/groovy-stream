package groovy.stream

public class DelegateTests extends spock.lang.Specification {
    def "unfilteredIndex map test"() {
        setup:
            def stream = Stream.from 1..4 map { it * unfilteredIndex }
        when:
            def result = stream.collect()
        then:
            result == [ 0, 2, 6, 12 ]
    }

    def "unfilteredIndex filter test"() {
        setup:
            def stream = Stream.from 1..4 filter { 2 != unfilteredIndex }
        when:
            def result = stream.collect()
        then:
            result == [ 1, 2, 4 ]
    }

    def "unfilteredIndex until test"() {
        setup:
            def stream = Stream.from 1..4 until { unfilteredIndex == 2 }
        when:
            def result = stream.collect()
        then:
            result == [ 1, 2 ]
    }
}