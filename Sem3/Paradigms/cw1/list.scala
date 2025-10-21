object tes extends App {
  // 1
  private def flatten1[A](xss: List[List[A]]): List[A] = if(xss.isEmpty) then Nil else xss.head ++ flatten1(xss.tail);
  //2
  private def count[A] (x: A, xs: List[A]): Int = if (xs.isEmpty) then 0 else if (xs.head.equals(x)) then 1 + count(x, xs.tail) else 0 + count(x,xs.tail);
  //3
  private def replicate[A] (x: A, n: Int): List[A] = if (n < 0) then Nil else List(x) ++ replicate(x, n - 1);
  //4
  private def sqrList (xs: List[Int]): List[Int] = if(xs.isEmpty) then Nil else List(xs.head * xs.head) ++ sqrList(xs.tail);
  //5
  private def palindrome[A] (xs: List[A]) : Boolean = xs.equals(xs.reverse);
  //6
  private def listLength[A](xs: List[A]) : Int = if (xs.isEmpty) then 0 else 1 + listLength(xs.tail);

  println(flatten1(List(List(5, 6), List(1, 2, 3))));
  println(count("n", List("n", "n", "o")));
  println(replicate("a", 11))
  println(sqrList(List(-1,3,5,0,22)))
  println(palindrome(List("a", "l", "a")))
  println(listLength(List(1,2,3,4,5,6)))

}