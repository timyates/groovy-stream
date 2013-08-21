package groovy.stream

import groovy.stream.Stream

class StreamTests extends spock.lang.Specification {
    def "test simple stream"() {
        setup:
            def s = Stream.from { 1 }

        when:
            def result = s.take( 5 ).collect()

        then:
            result == [ 1, 1, 1, 1, 1 ]
    }

    def "test simple mapping stream"() {
        setup:
            def s = Stream.from { 1 }.map { it * 2 }

        when:
            def result = s.take( 5 ).collect()

        then:
            result == [ 2, 2, 2, 2, 2 ]
    }

    def "test simple termination"() {
        setup:
            def s = Stream.from( [ 1, 2, 3 ] ).until { it > 1 }

        when:
            def result = s.take( 5 ).collect()

        then:
            result == [ 1 ]
    }

    def "test index appender"() {
        setup:
            def list = [ 1, 2, 3 ]
            def stream = Stream.from list map { [ it, idx++ ] } using idx:0

        when:
            def result = stream.collect()

        then:
            result == [ [ 1, 0 ], [ 2, 1 ], [ 3, 2 ] ]
    }

    def "test arrays"() {
        setup:
            int[] array = 1..3
            def stream = Stream.from array map { [ it, idx++ ] } using idx:0

        when:
            def result = stream.collect()

        then:
            result == [ [ 1, 0 ], [ 2, 1 ], [ 3, 2 ] ]
    }

    def "Map with transformation and limits"() {
        setup:
            def stream = Stream.from x:1..3, y:1..3 filter { x == y } map { x + y }

        when:
            def result = stream.collect()

        then:
            result == [ 2, 4, 6 ]
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
            def stream = Stream.from x:1..3, y:'a'..'b' filter { x % 2 == 0 }
        when:
            def result = stream.collect()
        then:
            result == [ [x:2,y:'a'], [x:2,y:'b'] ]
    }

    def "test 4"() {
        setup:
            def stream = Stream.from x:1..3, y:'a'..'b' map { [ x:x*2, y:"letter $y" ] }
        when:
            def result = stream.collect()
        then:
            result == [ [x:2,y:'letter a'], [x:2,y:'letter b'],
                        [x:4,y:'letter a'], [x:4,y:'letter b'],
                        [x:6,y:'letter a'], [x:6,y:'letter b'] ]
    }

    def "test 5"() {
        setup:
            def stream = Stream.from 'a'..'c' map { [ idx++, it ] } using idx:0
        when:
            def result = stream.collect()
        then:
            result == [ [0,'a'], [1,'b'], [2,'c'] ]
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