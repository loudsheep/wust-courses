trait Debug {
  def debugVars(): Unit = {
    val classObj = this.getClass;
    println(f"Class name: ${classObj.getSimpleName}")

    for(field <- classObj.getDeclaredFields) {
      field.setAccessible(true);
      println(s"Field: ${field.getName} => ${field.getType.getSimpleName} = ${field.get(this)}")
    }

    println("=========")
  }
}

class Point(xv: Int, yv: Int) extends Debug {
  var x: Int = xv
  var y: Int = yv
  var a: String = "test"
}

class Student(name: String, passed: Boolean) extends Debug {
  val studentName: String = name
  var avgGrade = 4.5
  private val isActive = passed
}

class Empty() extends Debug

@main
def main(): Unit = {
  val p = new Point(10, -45);
  p.debugVars();

  val s = new Student("Jan Kowalski", false);
  s.debugVars();

  val e = new Empty();
  e.debugVars();
}