## What is it ?

This project is a simple yet functional prototype to port a subset of [ScalaCL](https://code.google.com/p/scalacl/) / [Scalaxy](https://github.com/ochafik/Scalaxy)'s loop rewrites to Scala 2.10.

The goal is to integrate it into Scala, which should be relatively easy.

In the meanwhile, it supports foreach comprehensions of special Ranges that are automagically rewritten to while loops:

	import scala.inlinable._
	
    for (i <- 0 to_ 1000 by -2) {
    	println(i)
    }

Filters are not supported yet, but nested loops pose no problem (the major problem here is to remember to put that nasty `_` suffix to `to_` and `until_` :-S).
    
## Build / Test

Please do yourself a favor and use [paulp/sbt-extras](https://github.com/paulp/sbt-extras)'s [sbt script](https://raw.github.com/paulp/sbt-extras/master/sbt).

Run the tests with:

    sbt ~test
    
## What's next ?

Let's see what the feedback is first, in particular from Scala Compiler gurus.

Ideally, this should be merged into Scala (scala.inlinable.InlineableRange should just blend seamlessly into Range).

Also, for ideas of future developments, checkout which optimizations [ScalaCL supports](https://code.google.com/p/scalacl/wiki/ScalaCLPlugin#General_optimizations), for instance. 

## Where can this project be discussed ?

- From [this discussion on scala-internals](https://groups.google.com/d/topic/scala-internals/7KKEMl8gWKk/discussion)
- On Twitter: [@ochafik](https://twitter.com/ochafik)
- Here: fork, modify, submit pull-requests!
