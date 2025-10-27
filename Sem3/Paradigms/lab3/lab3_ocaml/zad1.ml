let rev arr =
  let rec aux acc = function
    | [] -> acc
    | x :: xs -> aux (x :: acc) xs
  in
  aux [] arr

let divide3 arr =
  let rec aux i (a0, a1, a2) = function
    | [] -> (rev a0, rev a1, rev a2)
    | x :: xs ->
      if i mod 3 = 0 then aux (i + 1) (x :: a0, a1, a2) xs
      else if i mod 3 = 1 then aux (i + 1) (a0, x :: a1, a2) xs
      else aux (i + 1) (a0, a1, x :: a2) xs
  in
  aux 0 ([], [], []) arr


let test1 = divide3 [5;4;3;2] = ([5;2], [4], [3])
let test2 = divide3 [] = ([], [], [])
let test3 = divide3 [10] = ([10], [], [])
let test4 = divide3 [1;2;3;4;5;6] = ([1;4], [2;5], [3;6])
let test5 = divide3 [9;8;7;6;5;4;3;1;2] = ([9;6;3], [8;5;1], [7;4;2])

let _ =
  print_endline (string_of_bool test1);
  print_endline (string_of_bool test2);
  print_endline (string_of_bool test3);
  print_endline (string_of_bool test4);
  print_endline (string_of_bool test5);