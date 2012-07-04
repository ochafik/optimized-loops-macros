## What is it ?

This project is a simple yet functional prototype to port [ScalaCL](https://code.google.com/p/scalacl/) / [Scalaxy](https://github.com/ochafik/Scalaxy)'s loop rewrites to Scala 2.10.

The goal is to integrate it into Scala, which should be relatively easy.

In the meanwhile, it supports foreach comprehensions of modified Ranges that are automagically rewritten to while loops:

    for (i <- 0 to_ 1000 by -2) {
    	println(i)
    }

## Build / Test

Please do yourself a favor and use [paulp/sbt-extras](https://github.com/paulp/sbt-extras)'s [sbt script](https://raw.github.com/paulp/sbt-extras/master/sbt).

Run the tests with:

    sbt ~test
    
## What's next ?

Let's see what the feedback is first, in particular from Scala Compiler gurus.

Ideally, this should be merged into Scala (scala.inlinable.InlineableRange should just blend seamlessly into Range).

Also, for ideas of future developments, checkout which optimizations [ScalaCL supports](https://code.google.com/p/scalacl/wiki/ScalaCLPlugin#General_optimizations), for instance. 
