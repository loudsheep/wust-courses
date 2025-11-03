let f tuples =
  let join4 (a, b, c, d) = a ^ b ^ c ^ d in
  List.map join4 tuples

let test1 = f [("a", "b", "c", "d"); ("1", "2", "3", "4")] = ["abcd"; "1234"]
let test2 = f [] = []
let test3 = f [("hello", " ", " ", "!")] = ["hello  !"]
let test4 = f [("", "", "", "")] = [""]

let _ =
  print_endline (string_of_bool test1);
  print_endline (string_of_bool test2);
  print_endline (string_of_bool test3);
  print_endline (string_of_bool test4);