---
layout: page
title : Releases
header : News about each release of groovy-stream
group: navigation
---
{% include JB/setup %}

### v0.7.0-SNAPSHOT ([code here](https://github.com/timyates/groovy-stream/tree/development))

{% highlight groovy linenum %}
@GrabResolver( name='so', root='https://oss.sonatype.org/content/repositories/snapshots/' )
@Grab( 'com.bloidonia:groovy-stream:0.7.0-SNAPSHOT' )
import groovy.stream.*
{% endhighlight %}

 - Removed the `using` block. This can be handled externally by the calling scope, and was adding much complexity to the Stream code.

{% highlight groovy linenum %}
def quantities = [ eggs:3, ham:2, bread:4 ]
def result = Stream.from( [ 'ham', 'bread', 'eggs' ] )
                   .flatMap { [ it ] * quantities[ it ] }          // so 'ham' will return [ 'ham', 'ham' ]
                   .filter { it != 'bread' }                       // get rid of bread
                   .join( ',' )                                    // Join into a string
{% endhighlight %}

 - Added `skip` method to skip `n` elements in the Stream

{% highlight groovy linenum %}
assert Stream.from( 1..5 ).skip( 3 ).collect() == [ 4, 5 ]
{% endhighlight %}

 - Added `zip` method which takes an Iterator (or another Stream), and executes a 2 parameter Closure that is passed the next elements from each Iterator. The result of this Closure is passed on down the Stream. The stream will end when *either* iterator is exhausted.

{% highlight groovy linenum %}
// One stream of 3 elements, one of 4
def letters = Stream.from 'a'..'c'
def numbers = Stream.from 1..4
assert letters.zip( numbers ) { a, b -> "$a$b" }
              .collect() == [ 'a1', 'b2', 'c3' ]


// other way round, same result
letters = Stream.from 'a'..'c'
numbers = Stream.from 1..4
assert numbers.zip( letters ) { a, b -> "$a$b" }
              .collect() == [ '1a', '2b', '3c' ]
{% endhighlight %}


 - Added `concat` method which takes an Iterator, and once the current stream is exhausted, will start returning items from this.

{% highlight groovy linenum %}
def letters = Stream.from 'a'..'c'
def numbers = Stream.from 1..4
assert letters.concat( numbers ).collect() == [ 'a', 'b', 'c', 1, 2, 3, 4 ]
{% endhighlight %}
    
 - Added `withIndex` methods, ie: `filterWithIndex`, `flatMapWithIndex`, `tapWithIndex`, `tapEveryWithIndex`, `mapWithIndex`, `untilWithIndex` and `zipWithIndex`.  Most of these take a 2 argument Closure (3 arguments in the case of `zipWithIndex`) in which the last parameter will be the current index at this point in the Stream.
 
### v0.6.2

- If `collate` was called with `keepRemainder` set to false, but there would be
    no remainder, then a NPE was thrown.

### v0.6.1

- Streams and Iterators all return `NoSuchElementException` if `next()` is called when exhausted.
- Added `tap` and `tapEvery` for tapping into the stream every `n` elements
- Fixed an issue with nulls in the stream

### v0.6

The groovy-stream has been almost entirely rewritten so it's hopefully easier to maintain and extend.

The main difference now is that you can have multiple `filter` and `map` steps and they are executed
in the same order they are added to the Stream.  ie:

{% highlight groovy linenum %}
@Grab( 'com.bloidonia:groovy-stream:0.6' )
import groovy.stream.*

def result = Stream.from( 1..50 )
                                     .filter { it % 5 == 0 }            // just the multiples of 5
                                     .map    { 100 / it    }            // as a divisor of 100
                                     .filter { it == Math.round( it ) } // Just the integers
                                     .map    { "#$it" }                 // Convert to a String
                                     .collect()

assert result == ['#20', '#10', '#5', '#4', '#2']
{% endhighlight %}

Accordingly as there is no implicit order things are fired any more, the `unfilteredIndex` is updated
before any steps are executed, and the `streamIndex` variable is updated after all steps have fired.

We also have a new step `flatMap`, which returns a `Collection`, and these values are passed individually
through the following steps before a new value is fetched from the source, ie:

{% highlight groovy linenum %}
@Grab( 'com.bloidonia:groovy-stream:0.6' )
import groovy.stream.*

def result = Stream.from( [ 'ham', 'bread', 'eggs' ] )
                   .flatMap { [ it ] * quantities[ it ] }          // so 'ham' will return [ 'ham', 'ham' ]
                   .filter { it != 'bread' }                       // get rid of bread
                   .using( quantities:[ eggs:3, ham:2, bread:4 ] ) // Our quantities
                   .join( ',' )                                    // Join into a string

assert result == 'ham,ham,eggs,eggs,eggs'
{% endhighlight %}

Also, added a `collate` method to Streams (same params as the Groovy `collate` method on `List`).  Allows you to do:

{% highlight groovy linenum %}
@Grab( 'com.bloidonia:groovy-stream:0.6' )
import groovy.stream.*

def result = Stream.from( 1..10 )
                                     .collate( 3 )
                                     .collect()
                                     
assert result == [ [ 1, 2, 3 ], [ 4, 5, 6 ], [ 7, 8, 9 ], [ 10 ] ]
{% endhighlight %}

### v0.5.4

`Stream` now implements `Iterable<T>`

This lets you (once 0.5.4 hits maven) use streams as observables with [RxJava](https://github.com/Netflix/RxJava), ie:

{% highlight groovy linenum %}
@Grab('com.bloidonia:groovy-stream:0.5.4')
import groovy.stream.*
@Grab('com.netflix.rxjava:rxjava-groovy:0.5.2')
import rx.*

def integers = Stream.from 1..50
Observable.toObservable( integers )
                    .skip( 10 )
                    .take( 5 )
                    .map { "Number $it" }
                    .subscribe { println "onNext => " + it }
{% endhighlight %}

Which should print:

    onNext => Number 11
    onNext => Number 12
    onNext => Number 13
    onNext => Number 14
    onNext => Number 15
                    
### v0.5.3

Fixed a [slight issue](https://github.com/timyates/groovy-stream/issues/11) where calling `next()` on a Stream before calling `hasNext()` would cause a `NullPointerException`

Although calling `next()` before calling `hasNext()` feels a bit free and loose as programming goes, it was probably wrong ;-)

### v0.5.2

The library now works with Java 6 (thanks to [Andres Almiray](https://twitter.com/aalmiray) for [spotting this one](https://github.com/timyates/groovy-stream/issues/8))


### v0.5.1

Fixed a bug whereby Streams couldn't iterate another Stream.

### v0.5

The functions where and transform have been renamed to `filter` and `map` respectively.

It's better to use existing, known function names for existing functionality rather than inventing your own terms.

### v0.4

You can now use arrays as a source for groovy-streams. Previously, running:

{% highlight groovy linenum %}
int[] arr = [ 1,2,3 ]
Stream s = Stream.from arr
s.each { println it }
{% endhighlight %}

Would be an infinite loop, returning `[1,2,3]` every time.

This will now behave as expected.

### v0.3

groovy-stream can now be used as a Module Extension with Groovy 2.0

This means you can do:

{% highlight groovy linenum %}
Stream s = [1,2,3].toStream()
{% endhighlight %}

Just by including the built jar on your classpath. It works with `@Grab` too:

{% highlight groovy linenum %}
@GrabResolver( name='bloidonia', root='https://raw.github.com/timyates/bloidonia-repo/master' )
@Grab('com.bloidonia:groovy-stream:0.3')
import groovy.stream.Stream 

def s = (1..4).toStream()
assert [1,2,3,4] == s.collect()
{% endhighlight %}

See `groovy.stream.StreamExtension` for the decorated classes.

Currently (as of Groovy 2.0RC4) there are issues with using @Grab -- (see GROOVY-5543 but this should work fine where the jar is added to the classpath by hand.

### v0.2

When Streaming over a Map, it is now passed as a parameter to the where block so you can deal with shadowed variables, ie:

{% highlight groovy linenum %}
def x = 10

def bad = Stream.from x:1..3, y:1..3 where { x == y }
assert bad.collect() == [] // as 'x' in the where block == 10

def good = Stream.from x:1..3, y:1..3 where { it.x == it.y }
assert good.collect() == [ [x:1,y:1],[x:2,y:2],[x:3,y:3] ]
{% endhighlight %}

### v0.1

Initial release.
