sealed trait BT[+A]

case object Empty extends BT[Nothing]

case class Node[+A](elem: A, left: BT[A], right: BT[A]) extends BT[A]

def tree_sum(tree: BT[Int]): Int = {
  @annotation.tailrec
  def loop(queue: List[BT[Int]], acc: Int): Int = queue match {
    case Nil => acc
    case Empty :: tail => loop(tail, acc)
    case Node(elem, left, right) :: tail =>
      loop(left :: right :: tail, acc + elem)
  }

  loop(List(tree), 0)
}


@main
def main(): Unit = {
  val tt = Node(1,
    Node(2,
      Node(4, Empty, Empty),
      Empty
    ),
    Node(3,
      Node(5,
        Empty,
        Node(6, Empty, Empty)
      ),
      Empty
    )
  )
  println(s"Test 1: ${tree_sum(tt)}")

  println(s"Test 2: ${tree_sum(Empty)}")
  
  val t3 = Node(10, Node(5, Empty, Empty), Node(15, Empty, Empty))
  println(s"Test 3: ${tree_sum(t3)}")

  val t4 = Node(1, Node(1, Node(1, Empty, Empty), Empty), Empty)
  println(s"Test 4: ${tree_sum(t4)}")
}