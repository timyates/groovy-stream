---
layout: page
title : Releases
header : News about each release of groovy-stream
group: navigation
---
{% include JB/setup %}

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