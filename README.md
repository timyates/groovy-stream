### A Groovy class for lazy list comprehension and generation.

>Under construction...  The code needs stabilising, and this is just a proof of concept to keep me busy in my spare time ;-)

To see usage, have a look in the [`src/test/groovy/groovy/stream` folder](https://github.com/timyates/groovy-stream/tree/master/src/test/groovy/groovy/stream), but in a nutshell, this class lets you do stuff like:

examples:

```groovy
Stream s = Stream.from x:1..2, y:1..4 where { x + y == 4 }

assert s.collect() == [ [ x:1, y:3 ], [ x:2, y:2 ] ]
```

or


```groovy
Stream s = Stream.from 1..10 transform { it + x++ } where { it % 2 } using x:0

assert s.collect() == [ 1, 4, 7, 10, 13 ]
```

```groovy
// Odd example but showing all of the features

Stream.from { 1..max } transform { -it } where { it >= min } using min:3, max:5

assert s.collect() == [ -3, -4, -5 ]
```