## Higher-order Functions

Before we get into currying and partially applied functions, we'll need to understand higher-order functions.  Functions are values.  This means that they can be assigned to variables, passed as arguments to a function, and stored in data structures.  So, a function that accepts another function as an argument is called a higher-order function.  Let's look at an example:

```scala

def doStuff(name: String, num: Int, num2: Int, f: Int => Int) = {
	val message = " %d %s %d is %d"
	messsage.format(name, num, num2, f(num,num2))
}

def add(n: Int, n1: Int) = {
	n + n1
}
def subtract(n: Int, n1: Int) = {
	n - n1
}

```


Both our add and subtract functions take in an int as argument and return an int ( Int => Int).  Therefore we can pass both add and subtract into ```doStuff``` as an argument:


```scala
doStuff("+", 5,7,add) // "5 + 7 is 12"
doStuff("-", 10,3,add) // "10 - 3 is 7"
```

### Anonymous Higher-order Functions


We can also declare and pass in anonymous functions in line:

```scala
doStuff("+", 1, 2, (x: Int, y:Int) => x + y ) // "1 + 2 is 3"
```

Notice that the type is inferred from the arguments given.



## Partially applied functions


A partially applied function is one that is being applied to some, but not all of the arguments it requires.  A partial function takes a value and a function of two arguments and returns a function of one argument as a result.  For example:


```scala

def arith1(a: Int, b:Int, c:Int): Int = {
	a + b * c
}

def partial =  (a: Int, b: Int) => arith1(a, b, 3)


partial(1,2) // 7
```


The signature of ```partial``` is (Int,Int) => Int.  The function ```partial``` will implicitly partially apply arith1.  Whenever we call ```partial```, the third argument ```c``` has already been applied. We can now call partial with 2 arguments, ```a,b```.

## Currying


Currying is the process of taking a function with multiple arguments and breaking it down into a chained sequence of  functions with one argument.  For example:


```scala

def arith1(a: Int, b:Int, c:Int): Int = {
	a + b * c
}

def curriedArith1(a:Int)(b:Int)(c:Int): Int = {
	arith1(a,b,c)
}

def exampleA(a:Int):Int = {
	curriedArith1(a)(2)(3)
}

exampleA(1) // 7
```

The signature of ```curriedArith1``` is Int =>(Int => (Int => Int)).  In other words, ```curriedArith1``` takes an int as parameter and returns a function that takes an integer and so on.


A shortcut to currying a function is by using the ```curried``` method:

```scala
def arith1(a: Int, b:Int, c:Int): Int = {
	a + b * c
}

def curriedArith1 = (arith1, _).curried

```




