type eval = Num of float | Add | Sub | Mul | Div;;

let eval instructions = 
  let rec aux ins stack = 
    match (ins, stack) with
    (* Bład bo nie ma nic na stosie, a powinien być wynik *)
    | ([], []) -> failwith "Empty instruction list and empty stack"
    (* Koniec instruukcji, zwracamy góre stosu *)
    | ([], result :: _) -> result

    (* Dodanie liczby na stos *)
    | (Num n :: t, st) -> aux t (n :: st)

    (* Operacje arytmetyczne *)
    | (Add :: t, b :: a :: st) -> aux t ((a +. b) :: st)
    | (Sub :: t, b :: a :: st) -> aux t ((a -. b) :: st)
    | (Mul :: t, b :: a :: st) -> aux t ((a *. b) :: st)
    | (Div :: t, b :: a :: st) -> 
      if b = 0.0 then failwith "Division by zero"
      else aux t ((a /. b) :: st)

    (* brak liczb na stoise, inne błędy *)
    | (_ :: _, _) -> failwith "Not enough values on the stack for operation"
  in
  aux instructions [];;

let _ =
  let instuctions1 = [Num 12.0; Num 4.0; Num 2.0; Add; Num 2.0; Mul; Sub] in
  let result1 = eval instuctions1 in
  Printf.printf "Result 1: %f\n" result1;

  let instuctions2 = [Num 0.0; Num 0.0; Add] in
  let result2 = eval instuctions2 in
  Printf.printf "Result 2: %f\n" result2;

  let instuctions3 = [Num 5.0; Num 4.0; Div] in
  let result3 = eval instuctions3 in
  Printf.printf "Result 3: %f\n" result3;

  let instuctions4 = [Num 10.0; Num 2.0; Div] in
  let result4 = eval instuctions4 in
  Printf.printf "Result 4: %f\n" result4;

  let instuctions5 = [Num 2.0; Num 3.0; Mul; Num 4.0; Sub] in
  let result5 = eval instuctions5 in
  Printf.printf "Result 5: %f\n" result5;