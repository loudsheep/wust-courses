object GenList {
  def genlist(a: Int, b: Int): List[Int] = {
    if (a > b) genlist(b, a)
    else if (a == b) List(a)
    else a :: genlist(a + 1, b)
  }

  def main(args: Array[String]): Unit = {
    println(genlist(4, 8))
    println(genlist(8, 4))
    println(genlist(3, 3))
    println(genlist(-2, 2))
    println(genlist(10, 13))
  }
}

