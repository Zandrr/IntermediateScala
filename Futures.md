## Futures

In Scala, a future is the promise of a result of a computation that has not yet completed. Futures extend the idea of a Try to handle asynchronicity.  A future can be in one of three states: pending, failed or succeeded.  These futures can only transition once.  I will be talking about Twitter's implementation of futures as it is a precursor to using Finagle.


### Obtaining value of a future - the dumb way.

```scala

Await.result()

```


This accepts a future as an argument.  It also blocks the current thread.  This is bad.  Really bad.  We don't want our current thread to be blocked every time we use a future.  Only if there was some way to handle this... Finagle, which is:

> "Finagle is an extensible RPC system for the JVM, used to construct high-concurrency servers. Finagle implements uniform client and server APIs for several protocols, and is designed for high performance and concurrency. Most of Finagle’s code is protocol agnostic, simplifying the implementation of new protocols."

Finagle will manage our threads, and manage the RPC between our services.  In other words, your code does not do the ```Await```, finagle will handle that. Finagle is detecting when a future is finally resolved, and will simply send it back to the client.


Let's look at some examples:

Let's say we have some function: ```getStockPrice(str: String)``` which returns the current stock price of the given code.  Future supports 3 callbacks that we can use: ```onSuccess()```, ```onFailure()```, ```ensure()```.


```scala

val ret: Future[Int] = getStockPrice("APPL")
ret.onSuccess {
	i: Int => //val of our successful future
}.onFailure {
	e: Throwable => //throwable of failure
}.ensure{
	//something we want to happen regardless of throw/success
}

```

Note that none of these functions cause any side-effects.  All the changes made by the functions are thrown away after the function returns.


More examples!


```scala

scala> import com.twitter.util.{Future,Promise}
import com.twitter.util.{Future, Promise}

scala> val f = Future.value(1)
f: com.twitter.util.Future[Int] = com.twitter.util.ConstFuture@3c4208bf

scala> f.get()
res0: Int = 1

scala> val promise1 = new Promise[Int]
promise1: com.twitter.util.Promise[Int] = Promise@1662603536(state=Waiting(null,List()))

scala> promise1.get() //this will hang waiting for the future to resolve!

scala> promise1.setValue(55)

scala> promise1.get()
res1: Int = 55

```

Normally we won't use ```get()``` in our code, we will use callbacks.


### Sequential Compositions of Futures


Futures support ```map()```, ```flatMap()```, ```handle()```, and ```rescue()```, just like ```Try``` does.

When using sequential compositions, be sure not to block threads!


```scala
val future1 = f.map {n => n.toString} //this is fine because n.toString  doesn't block on I/O

val future2 = f.map {n => blockIO(n)} //this is bad because we block on our current thread

```


### Concurrent Composition

What if we want to grab data from more than one service at one time?  Have no fear, ```collect()```, ```join()```, and ```select()``` are here!


Collect takes a set of futures and yields a Seq of values of that type:


```scala

scala> val f1 = Future.value(10)
f1: com.twitter.util.Future[Int] = com.twitter.util.ConstFuture@1ada529f

scala> val f2 = Future.value(20)
f2: com.twitter.util.Future[Int] = com.twitter.util.ConstFuture@4bc86f15

scala> val f12 = Future.collect(Seq(f1,f2))
f12: com.twitter.util.Future[Seq[Int]] = Promise@221958947(state=Done(Return(ArrayBuffer(10, 20))))

scala> f12
res2: com.twitter.util.Future[Seq[Int]] = Promise@221958947(state=Done(Return(ArrayBuffer(10, 20))))

scala> f12.get()
res3: Seq[Int] = ArrayBuffer(10, 20)

scala> val ret = f12.map{n => n.sum}
ret: com.twitter.util.Future[Int] = Promise@849517840(state=Done(Return(30)))

scala> ret
res4: com.twitter.util.Future[Int] = Promise@849517840(state=Done(Return(30)))

scala> ret.get()
res5: Int = 30

```

```join()``` takes a sequence of Futures whose types may be different, yielding a Future[Unit].  We can't ```get()``` the value, but we know it's done.


```scala

scala> val rdy = Future.join(Seq(f1,f2))
rdy: com.twitter.util.Future[Unit] = Promise@59898988(state=Done(Return(())))

scala> f1
res6: com.twitter.util.Future[Int] = com.twitter.util.ConstFuture@1ada529f

scala> rdy.get() // returns silently

````


```select()``` returns a completed future when the first of the given futures has completed.  We can wait longer for the other futures to return, but we don't need to.


```scala

scala> val pr1 = Promise[Int]
pr1: com.twitter.util.Promise[Int] = Promise@597588856(state=Waiting(null,List()))

scala> val prnew1 = new Promise[Int]
prnew1: com.twitter.util.Promise[Int] = Promise@1222865968(state=Waiting(null,List()))

scala> val select1 = Future.select(Seq(f1, pr1))
select1: com.twitter.util.Future[(com.twitter.util.Try[Int], Seq[com.twitter.util.Future[Int]])] = Promise@1137933319(state=Done(Return((Return(10),List(Promise@597588856(state=Waiting(<function1>,List())))))))

scala> val(done,notdone) = select1.get()
done: com.twitter.util.Try[Int] = Return(10)
notdone: Seq[com.twitter.util.Future[Int]] = List(Promise@597588856(state=Waiting(<function1>,List())))

scala> done.get()
res8: Int = 10

scala> notdone(0).get()

scala> notdone.setValue(22)

scala> notdone(0).get()
res7: Int: 22

```


Notice that we can ```select1.get()``` before ```notdone``` has finished.  Once it has resolved, we can get it at a later time!

