/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    def 'rock paper scissors'() {

        "http://blog.bloidonia.com/post/55170995645/rock-paper-scissors-with-groovy-stream"

        setup:
            // Seeded random (for testing)
            Random rnd = new Random( 1 )

            // A list of possible moves
            List moves = [ 'rock', 'paper', 'scissors' ]

            // A map showing whch moves beat which
            Map beats  = [ 'rock'     : 'scissors',
                           'paper'    : 'rock',
                           'scissors' : 'paper' ]

            // Each player is a Stream of moves
            def player = { String name ->
                Stream.from { [ player:name, move:moves[ rnd.nextInt( moves.size() ) ] ] }
            }

            // A Closure that declares a winner between two moves
            def winner = { move1, move2 ->
                def ret = "$move1.move vs $move2.move : "
                if( move1.move == move2.move )               { ret + 'no one wins' }
                else if( move2.move == beats[ move1.move ] ) { ret + "$move1.player wins" }
                else                                         { ret + "$move2.player wins" }
            }

            // Create a never-ending stream that gives us the winner of the two players
            def judge = Stream.from { 1 }
                              .map { winner( p1.next(), p2.next() ) }
                              .using p1: player( 'tim' ), p2: player( 'alice' )
        when:
            def result = judge.take( 3 ).collect()

        then:
            result == [ 'rock vs paper : alice wins', 
                        'paper vs rock : tim wins',
                        'scissors vs paper : tim wins' ]
    }

    def 'lazy squares'() {

        "http://blog.bloidonia.com/post/41103158982/lazy-squares-using-groovy-stream"

        setup:
            // Create a lazy unending stream of integers from 1
            Stream integers = Stream.from { x++ } using x:1
             
            // Create a stream of squares based on this stream of integers
            Stream squares  = Stream.from integers map { it * it }

        when:
            // Create an Iterator that looks at the first 25 elements of squares
            Iterator first25 = squares.take( 25 )

        then:             
            // Collect the elements
            assert first25.collect() == [  1,   4,   9,  16,  25,
                                          36,  49,  64,  81, 100,
                                         121, 144, 169, 196, 225,
                                         256, 289, 324, 361, 400,
                                         441, 484, 529, 576, 625 ]
    }
}