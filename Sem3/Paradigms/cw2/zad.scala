import scala.annotation.tailrec

object cw2 extends App {

  // 2
  private def fib(x: Int): Int = {
    if (x <= 0) then 0
    else if (x == 1) then 1
    else fib(x - 1) + fib(x - 2)
  }
  println("The 3rd fib value: " + fib(3));
  println("The 5th fib value: " + fib(5));
  println("The 8th fib value: " + fib(8));
  println("The 23rd fib value: " + fib(23));

  private def fibTail(x: Int) : Int = {
    @tailrec
    def addNums(x1 : Int, x2: Int, i: Int) : Int = {
      if (i == 0) then x1 
      else addNums(x1 + x2, x1, i - 1);
    }
    if (x == 0) then 0 
    else if (x <= 2) then 1 
    else addNums(1,1, x - 2);
  }
  println("The 3rd fib value: " + fibTail(3));
  println("The 5th fib value: " + fibTail(5));
  println("The 8th fib value: " + fibTail(8));
  println("The 23rd fib value: " + fibTail(23));

  // 3
  private def root3(a: Double): Double = {
    val eps = 1e-15
    val x0 = if (a > 1) a / 3 else a

    @tailrec
    def iterate(x: Double): Double = {
      if (math.abs(x * x * x - a) <= eps * math.abs(a))
        x
      else {
        val next = x + (a / (x * x) - x) / 3
        iterate(next)
      }
    }

    iterate(x0)
  }
  // 4
  val list = List(-2, -1, 0, 1, 2)

  val x1 = list match {
    case List(-2, -1, v, 1, 2) => v
  }
  println(x1)


  val pairs = List((1, 2), (0, 1))

  val x2 = pairs match {
    case List((_, _), (v, _)) => v
  }
  println(x2)

  // 5
  private def initSegment[A](xs: List[A], xss: List[A]): Boolean = (xs, xss) match {
    case (Nil, _) => true
    case (_, Nil) => false
    case (xh :: xt, yh :: yt) =>
      if (xh == yh) initSegment(xt, yt)
      else false
  }

  // 6
  private def replaceNth[A](xs: List[A], n: Int, x: A): List[A] = (xs, n) match {
    case (Nil, _) => Nil
    case (_ :: tail, 0) => x :: tail
    case (head :: tail, k) => head :: replaceNth(tail, k - 1, x)
  }

}