import com.twitter.util.Future
import scala.util.control.NoStackTrace

object FuturesIntro {
  object Unimplemented extends Exception with NoStackTrace
  def ??? = throw Unimplemented

  // If the future is successful pass the value to the handle function.
  def handleSuccess(future: Future[String])(handle: String => Unit) {
    ???
  }

  // If the future fails pass the value to the handle function.
  def handleFailure(future: Future[String])(handle: Throwable => Unit) {
    ???
  }

  // Convert the value of the future to a String
  def convert(future: Future[Int]): Future[String] = {
    ???
  }

  // Sequence two futures. This simulates retrieving a value from a service
  // and using it to retrieve another value from a different service
  def sequenceCalls(future: Future[Int])(next: Int => Future[Int]): Future[Int] = {
    ???
  }

  // Calculate the sum of a sequence of futures.
  def sumSeq(futures: Seq[Future[Int]]): Future[Int] = {
    ???
  }

  // Take two Future[Int] and return their sum. (Don't use an intermediate
  // collection.
  def combineFutures(future1: Future[Int], future2: Future[Int]): Future[Int] = {
    ???
  }

  // Provide a default value on the case of a failure
  def turnExceptionToDefault(defaultValue: Int, future: Future[Int]): Future[Int] = {
    ???
  }
}