let duplicate arr counts =
  let rec repeat x n =
    if n <= 0 then []
    else x :: repeat x (n - 1)
  in
  let rec aux a c =
    match (a, c) with
    | (h1 :: t1, h2 :: t2) -> (repeat h1 h2) @ (aux t1 t2)
    | _ -> []
  in
  aux arr counts


let test1 = duplicate [1; 2; 3] [0; 3; 1; 4] = [2; 2; 2; 3]
let test2 = duplicate ["a"; "b"] [2; 0] = ["a"; "a"]
let test3 = duplicate [true; false] [1; 1] = [true; false]
let test4 = duplicate [5; 6; 7] [] = []
let test5 = duplicate [] [1; 2; 3] = []
let test6 = duplicate [1; 2] [-2; 2; 2; 2] = [2; 2]
let test7 = duplicate [] [] = []

let _ =
  print_endline (string_of_bool test1);
  print_endline (string_of_bool test2);
  print_endline (string_of_bool test3);
  print_endline (string_of_bool test4);
  print_endline (string_of_bool test5);
  print_endline (string_of_bool test6);
  print_endline (string_of_bool test7)