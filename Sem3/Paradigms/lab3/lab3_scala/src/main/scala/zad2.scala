def reverse(arr: List[Int]): List[Int] = {
  @annotation.tailrec
  def revTail(list: List[Int], acc:List[Int]): List[Int] = list match {
    case Nil => acc
    case h :: t => revTail(t, h :: acc)
  }

  revTail(arr, Nil)
}

def sumuj(l1: List[Int], l2: List[Int]): List[Int] = {
  @annotation.tailrec
  def sumujTail(a1: List[Int], a2: List[Int], acc: List[Int]): List[Int] = (a1, a2) match {
    case (Nil, Nil) => reverse(acc)
    case (a1h :: a1t, Nil) => sumujTail(a1t, Nil, (a1h + 0) :: acc)
    case (Nil, a2h :: a2t) => sumujTail(Nil, a2t, (0 + a2h) :: acc)
    case (a1h :: a1t, a2h :: a2t) => sumujTail(a1t, a2t, (a1h + a2h) :: acc)
  }
  sumujTail(l1, l2, Nil)
}

@main def main(): Unit = {
  println(sumuj(List(1, 2, 3), List(4, 5, 6, 7, 8))) // [5,7,9,7,8]
  println(sumuj(List(1, 2, 3), List(10, 20))) // [11,22,3]
  println(sumuj(Nil, List(1, 2, 3))) // [1,2,3]
  println(sumuj(List(1, 2, 3), Nil)) // [1,2,3]
  println(sumuj(Nil, Nil)) // []
}