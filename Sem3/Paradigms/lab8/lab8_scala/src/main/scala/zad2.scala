
def printAll(args: Any*): Unit = {
  def recursivePrint(argList: List[Any]): Unit = argList match {
    case Nil => ()
    case head :: tail =>
      val name = head.getClass.getName
      println(s"$name: $head")
      recursivePrint(tail)
  }
  recursivePrint(args.toList);
}

@main
def main(): Unit = {
  printAll(1, "hello", 3.14)
  println("--------------------")

  printAll()
  println("--------------------")

  printAll(1, 2, 3, 4, 'A')
  println("--------------------")

  printAll(List(1,2,3,4), None, Some(5))
  println("--------------------")
}