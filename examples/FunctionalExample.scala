def factorial(num: Int): Int = {
	def goRun(num: Int, accum: Int): Int = {
		if (num <= 0) accum
		else goRun(num-1, num*accum)
	}
	goRun(num,1)
}



def fib(n:Int): Int = n match{
	case 0 | 1 => n
	case _ => fib(n-1) + fib(n-2)
}