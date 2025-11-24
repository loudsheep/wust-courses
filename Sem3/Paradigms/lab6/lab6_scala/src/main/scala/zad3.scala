sealed trait Instr

case class Push(c: Char) extends Instr

case object Pop extends Instr

case object Upper extends Instr

case object Lower extends Instr

type Stack = List[Char]

def eval(instrs: List[Instr]): Stack = {
  instrs.foldLeft[Stack](List.empty) { (stack, instr) =>
    instr match {
      case Push(c) =>
        c :: stack
      case Pop =>
        stack match {
          case _ :: tail => tail
          case Nil => Nil
        }
      case Upper =>
        stack match {
          case head :: tail => Character.toUpperCase(head) :: tail
          case Nil => Nil
        }
      case Lower =>
        stack match {
          case head :: tail => Character.toLowerCase(head) :: tail
          case Nil => Nil
        }
    }
  }
}

@main
def main(): Unit = {
  val instrs1 = List(Push('a'), Push('b'), Upper, Pop, Push('c'), Upper)
  val test1 = eval(instrs1)

  val instrs2 = List(Push('x'), Pop, Pop, Upper)
  val test2 = eval(instrs2)

  val instrs3 = List(Push('d'), Push('E'), Lower, Push('f'), Upper)
  val test3 = eval(instrs3)

  val instrs4 = List(Push('a'), Push('b'), Push('c'), Pop, Pop)
  val test4 = eval(instrs4)

  val instrs5 = List(Push('a'), Push('b'), Push('c'))
  val test5 = eval(instrs5)

  println(s"Test 1: $test1")
  println(s"Test 2: $test2")
  println(s"Test 3: $test3")
  println(s"Test 4: $test4")
  println(s"Test 5: $test5")
}