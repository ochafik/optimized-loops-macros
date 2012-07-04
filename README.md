## What is it ?

This project is a simple yet fully functional prototype to port a subset of [ScalaCL](https://code.google.com/p/scalacl/) / [Scalaxy](https://github.com/ochafik/Scalaxy)'s loop rewrites to Scala 2.10.

The goal is to integrate it into Scala, which should be relatively easy.

In the meanwhile, it supports foreach comprehensions of special Ranges that are automagically rewritten to while loops:

	import scala.inlinable._
	
    for (i <- 100 to_ 0 by -2) {
    	println(i)
    }

Filters are not supported yet, but nested loops pose no problem (the major problem here is to remember to put that nasty `_` suffix to `to_` and `until_` :-S).
    
## Build / Test

Please do yourself a favor and use [paulp/sbt-extras](https://github.com/paulp/sbt-extras)'s [sbt script](https://raw.github.com/paulp/sbt-extras/master/sbt).

### Run the tests

Cases for all the variants of `start to_/until_ end [by step]` are tested, with positive and negative steps:

    sbt ~test
    
### Build / publish locally

    sbt publish-local

### Usage example

First, make sure to have built locally and run `sbt publish-local` (see above).

Create `build.sbt` as follows:

    scalaVersion := "2.10.0-M4"
    
    libraryDependencies += "com.nativelibs4java" %% "scala-inlinable" % "0.1-SNAPSHOT"
    
    // Uncomment the following option to examine the code after application of macros
    // (you'll see while loops instead of Range.foreach calls)
    //scalacOptions += "-Xprint:typer"

Put the following in `project/build.properties` (for paulp/sbt-extra's sbt script):

	sbt.version=0.12.0-RC3

Create `Test.scala`:

    import scala.inlinable._

	object Test extends App {    
		for (i <- 0 until_ 10) println(i)
		for (i <- 0 to_ 10 by 2) println(i)
	}
    
Run with:

	sbt clean run
	
You'll see the following output that confirms your loops were optimized:

	[info] /Users/ochafik/src/macrotest/Test.scala:4: Optimized this loop
	[info]   for (i <- 0 until_ 10) println(i)
	[info]          ^
	[info] /Users/ochafik/src/macrotest/Test.scala:5: Optimized this loop
	[info]   for (i <- 0 to_ 10 by 2) println(i)
	[info]          ^

	
## What's next ?

Let's see what the feedback is first, in particular from Scala Compiler gurus.

Ideally, this should be merged into Scala (scala.inlinable.InlineableRange should just blend seamlessly into Range).

Also, for ideas of future developments, checkout which optimizations [ScalaCL supports](https://code.google.com/p/scalacl/wiki/ScalaCLPlugin#General_optimizations), for instance. 

## Where can this project be discussed ?

- From [this discussion on scala-internals](https://groups.google.com/d/topic/scala-internals/7KKEMl8gWKk/discussion)
- On Twitter: [@ochafik](https://twitter.com/ochafik)
- Here: fork, modify, submit pull-requests!

## Additional notes

Haven't tried to figure things out yet, but the library should have a "self-erasing" behaviour: it's needed at compile time because it bundles its macros, but these macros should erase all traces of the library's types so it won't be needed in the classpath at runtime (to be confirmed, feedback is welcome). 

