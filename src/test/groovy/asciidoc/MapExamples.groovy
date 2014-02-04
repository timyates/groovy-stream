package asciidoc

import groovy.stream.*

import org.junit.Test

class MapExamples {

    @Test
    void testSimpleMap() {
        // tag::simple_example[]
        def stream = Stream.from( 1..3 )
                           .map { num -> "Item $num" }

        assert stream.collect() == [ 'Item 1', 'Item 2', 'Item 3' ]
        // end::simple_example[]
    }

    @Test
    void testIndexedMap() {
        // tag::index_example[]
        def stream = Stream.from( 1..3 )
                           .mapWithIndex { num, idx -> "Item $num @ $idx" }

        assert stream.collect() == [ 'Item 1 @ 0', 'Item 2 @ 1', 'Item 3 @ 2' ]
        // end::index_example[]
    }

    @Test
    void testFlatMap() {
        // tag::flat_example[]
        def stream = Stream.from( 1..3 )
                           .flatMap { num -> [ num, num ] }

        assert stream.collect() == [ 1, 1, 2, 2, 3, 3 ]
        // end::flat_example[]
    }

    @Test
    void testFlatIndexedMap() {
        // tag::flat_index_example[]
        def stream = Stream.from( 1..3 )
                           .flatMapWithIndex { num, idx -> [ num ] * ( idx + 1 ) }

        assert stream.collect() == [ 1, 2, 2, 3, 3, 3 ]
        // end::flat_index_example[]
    }
}