package asciidoc

import groovy.stream.*

import org.junit.Test

class BriefExamples {

    @Test
    void squares() {
        // tag::part_one[]
        def x = 1
        def integers = Stream.from { x++ }
        // end::part_one[]
        // tag::part_two[]
        def squares = Stream.from integers map { it * it }
        // end::part_two[]
        // tag::part_three[]
        def first5 = squares.take( 5 ).collect()
        assert first5 == [ 1, 4, 9, 16, 25 ]
        // end::part_three[]
    }

    @Test
    void listComprehension() {
        // tag::faux_comprehension[]
        def s = Stream.from( x:1..5, y:1..3 )
                      .filter { ( x + y ) % ( x + 2 ) == 0 }
                      .map { x + y }

        // Returns results for:
        //  3 - when - x:1, y:2 as (1+2)%(1+2) == 0
        //  4 - when - x:2, y:2 as (2+2)%(2+2) == 0
        //  5 - when - x:3, y:2 as (3+2)%(3+2) == 0
        //  6 - when - x:4, y:2 as (4+2)%(4+2) == 0
        //  7 - when - x:5, y:2 as (5+2)%(5+2) == 0
        assert s.collect() == [ 3, 4, 5, 6, 7 ]
        // end::faux_comprehension[]
    }
}