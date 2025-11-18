// ZADANIE 2

// Z lukrem syntaktycznym
def curry3[A, B, C, D](f: ((A, B, C)) => D): A => B => C => D = 
  x => y => z => f((x, y, z))

def uncurry3[A, B, C, D](f: A => B => C => D): ((A, B, C)) => D = 
  { case (x, y, z) => f(x)(y)(z) }

// Bez lukru (rozwinięcie definicji funkcji anonimowych)
def curry3_ns[A, B, C, D](f: ((A, B, C)) => D): Function1[A, Function1[B, Function1[C, D]]] = 
  new Function1[A, Function1[B, Function1[C, D]]] {
    def apply(x: A): Function1[B, Function1[C, D]] = new Function1[B, Function1[C, D]] {
      def apply(y: B): Function1[C, D] = new Function1[C, D] {
        def apply(z: C): D = f((x, y, z))
      }
    }
  }

def uncurry3_ns[A, B, C, D](f: A => B => C => D): Function1[(A, B, C), D] = 
  new Function1[(A, B, C), D] {
    def apply(t: (A, B, C)): D = f(t._1)(t._2)(t._3)
  }



// ZADANIE 3
def sumProd(xs: List[Int]): (Int, Int) =
  xs.foldLeft((0, 1)) { case ((s, p), x) => (s + x, p * x) }


// ZADANIE 4
// gubienie elementów o takiej samej wartości



// ZADANIE 5
def insertionsort[A](pred: (A, A) => Boolean, xs: List[A]): List[A] = {
  def insert(x: A, sorted: List[A]): List[A] = sorted match {
    case Nil => List(x)
    case h :: t =>
      // Stabilność: wstawiamy przed h tylko, jeśli x jest "bardziej pożądany" niż h
      // Zakładając, że pred(a,b) to a < b:
      if (pred(x, h)) x :: h :: t
      else h :: insert(x, t)
  }
  xs.foldLeft(List.empty[A])((acc, x) => insert(x, acc))
}

def mergesort[A](pred: (A, A) => Boolean, xs: List[A]): List[A] = {
  def merge(left: List[A], right: List[A]): List[A] = (left, right) match {
    case (Nil, r) => r
    case (l, Nil) => l
    case (lh :: lt, rh :: rt) =>
      // Stabilność: Pierwszeństwo dla elementu z lewej listy przy równości
      if (pred(lh, rh)) lh :: merge(lt, right)
      else rh :: merge(left, rt)
  }

  val n = xs.length / 2
  if (n == 0) xs
  else {
    val (l, r) = xs.splitAt(n) // splitAt w Scali jest bezpieczne i stabilne
    merge(mergesort(pred, l), mergesort(pred, r))
  }
}