let rec isSorted = function
  | [] | [_] -> true
  | x :: y :: rest -> x <= y && isSorted (y :: rest)


let insert x arr =
  if not (isSorted arr) then
    failwith "Array is not sorted"
  else
    let rec aux = function
      | [] -> [x]
      | h :: t -> if x <= h then x :: h :: t else h :: aux t
    in
  aux arr


let test1 = insert 4 [1; 3; 5; 7] = [1; 3; 4; 5; 7]
let test2 = insert 0 [2; 4; 6; 8] = [0; 2; 4; 6; 8]
let test3 = insert (-10) [1; 2; 3; 4] = [-10; 1; 2; 3; 4]
let test4 = insert 5 [] = [5]
let test5 = insert "c" ["b"; "c"; "d"] = ["b"; "c"; "c"; "d"]
let test6 =
  try
    let _ = insert 3 [5; 1; 4] in
    false
  with Failure _ -> true

let () =
  print_endline (string_of_bool test1);
  print_endline (string_of_bool test2);
  print_endline (string_of_bool test3);
  print_endline (string_of_bool test4);
  print_endline (string_of_bool test5);
  print_endline (string_of_bool test6)