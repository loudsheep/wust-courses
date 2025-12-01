
def ldzialanie(l1: LazyList[Int], l2: LazyList[Int], op: (Int, Int) => Int): LazyList[Int] = {
  (l1, l2) match {
    case (LazyList(), LazyList()) => LazyList()
    case (LazyList(), l2next) => l2next
    case (l1next, LazyList()) => l1next
    case (h1 #:: t1, h2 #:: t2) =>
      op(h1, h2) #:: ldzialanie(t1, t2, op)
  }
}


@main
def main(): Unit = {
  val l1 = LazyList(1, 2, 3)
  val l2 = LazyList(2, 3, 4, 5)
  val res1 = ldzialanie(l1, l2, (a, b) => a + b)
  println(res1.toList)

  val l3 = LazyList(2, 2, 2)
  val l4 = LazyList(3, 4, 5)
  val res2 = ldzialanie(l3, l4, (a, b) => a * b)
  println(res2.toList)

  val l5 = LazyList.from(0)
  val l6 = LazyList.continually(10)
  val res3 = ldzialanie(l5, l6, (a, b) => a + b)
  println(res3.take(5).toList)

  val l7 = LazyList()
  val l8 = LazyList()
  val res4 = ldzialanie(l7, l8, (a, b) => a * b)
  println(res4.toList)

  val l9 = LazyList(1, 2, 3, 4, 5, 6)
  val l10 = LazyList(11, 12, 13, 14, 15)
  val res6 = ldzialanie(l9, l10, (a, b) => 1)
  println(res6.toList)
}