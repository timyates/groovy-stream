### A Lazy Groovy Generator.

>Under construction...  The code needs stabilising, and this is just a proof of concept to keep me busy in my spare time ;-)

## New for v0.3

groovy-stream can now be used as a Module Extension with Groovy 2.0

This means you can do:

```groovy
Stream s = [1,2,3].toStream()
```

Just by including the built jar on your classpath. It works with `@Grab` too:

```groovy
@GrabResolver( name='bloidonia', root='https://raw.github.com/timyates/bloidonia-repo/master' )
@Grab('com.bloidonia:groovy-stream:0.3')
import groovy.stream.Stream 

def s = (1..4).toStream()
assert [1,2,3,4] == s.collect()
```

See [groovy.stream.StreamExtension](https://github.com/timyates/groovy-stream/blob/master/src/main/groovy/groovy/stream/StreamExtension.java) for the 4 classes that are decorated.

>Currently (as of Groovy 2.0RC4) this won't work in the Groovy Console due to a classpath issue, but hopefully it will soon :-)

---

**NEW**: There is a [blog post here](http://blog.bloidonia.com/post/22117894718/groovy-stream-a-lazy-generator-and-list-comprehension) explaining the state of groovy-stream v0.1 and it's usage

To see examples, have a look in the [`src/test/groovy/groovy/stream` folder](https://github.com/timyates/groovy-stream/tree/master/src/test/groovy/groovy/stream), but in a nutshell, this class lets you do stuff like:

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