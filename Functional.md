## What is functional programming?


Pure functional programming uses, you guessed it, *pure functions*.  A pure function is a one that has no **side effects**.  In other words, it's a function that does something other than return a result.  This includes, but is not limited to: Modifying a variable, doing I/O on a file, printing to the console, and modifying a data structure.


###A function *with side effects*:


```scala

class Calculator {
	def add(a: Int, b: Int) = {
		val c = a + b
		println(c)
		return c
	}
}

```


###A function *without side effects*:


```scala
class Calculator {
	def add(a: Int, b: Int) = {
		return a + b
	}
}

```



### Referential Transparency


In a nutshell, referential transparency is the idea that in any program, an expression can be replaced by its result *without changing the meaning of the program*.



###A referential example


```scala
val a = "Alex Campbell"
val ra = a.reverse // "llebpmaC xelA"

val ra2 = a.reverse // "llebpmaC xelA"
```


Now let's replace all occurrences of a with the expression that is being referenced by ```a```:


```scala
val ra = "Alex Campbell".reverse // "llebpmaC xelA"

val ra2 = "Alex Campbell".reverse // "llebpmaC xelA"
```


Notice that this transformation does not affect the outcome. ```ra``` and ```ra2``` are the same as they were before.  Therefore ```a``` is referentially transparent.  In addition, ```ra``` and ```ra2``` are also referentially transparent, as if they appeared in some other part of the program, they could be replaced with their values and it would not affect the overall result on the program.



###A non-referential example


```scala
val x = new StringBuilder("Alex") // "Alex"

val r1 = x.append(" Campbell").toString // "Alex Campbell"

val r2 = x.append(" Campbell").toString // "Alex Campbell Campbell"
```

This transformation results in two different outcomes.  Therefore, ```StringBuilder.append``` is not referentially transparent. ```r1``` and ```r2``` look like they are the exact same expression, but they are actually referencing two different values from the same ```StringBuilder``` object.  This is not a pure function!



###An example...


Let's look at a factorial function that is purely functional:


```scala


def factorial(num: Int): Int = {
	def goRun(num: Int, accum: Int): Int = {
		if (num <= 0) accum
		else goRun(num-1, num*accum)
	}
	goRun(num,1)
}

```

Although the inner function is changing num (reducing it by one for each iteration of recursion), it is not modifying anything in the outer function.



###Another example...

A recursive function to get the nth fibonacci number.

```scala

def fib(n:Int): Int = n match{
	case 0 | 1 => n
	case _ => fib(n-1) + fib(n-2)
}
```


This is another purely functional program as it is not modifying anything.