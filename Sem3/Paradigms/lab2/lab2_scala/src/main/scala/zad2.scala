import scala.annotation.tailrec

@tailrec
def length[A](arr: List[A], acc: Int = 0): Int = {
  if (arr.isEmpty) acc
  else length(arr.tail, acc + 1)
}

@main def main(): Unit = {
  val test1 = length(List(5, 4, 3, 2)) == 4
  val test2 = length(List("a", "b", "c")) == 3
  val test3 = length(List()) == 0
  val test4 = length(List(true, false, true, false, true)) == 5
  val test5 = length((1 to 1000000).toList) == 1000000

  println(s"Test 1: $test1")
  println(s"Test 2: $test2")
  println(s"Test 3: $test3")
  println(s"Test 4: $test4")
  println(s"Test 5: $test5")
}