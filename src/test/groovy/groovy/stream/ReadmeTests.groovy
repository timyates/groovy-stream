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
}
