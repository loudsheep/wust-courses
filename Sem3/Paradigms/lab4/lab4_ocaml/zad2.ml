let bin_to_dec bits = 
  let rec aux bits acc =
    match bits with
    | [] -> acc
    | h :: t -> aux t (acc * 2 + h)
  in
  aux bits 0

let test1 = bin_to_dec [1;0;0;1] = 9
let test2 = bin_to_dec [0;0;0;0] = 0
let test3 = bin_to_dec [1;1;1;1;1] = 31
let test4 = bin_to_dec [] = 0
let test5 = bin_to_dec [1;0;0;0;0;0;0;0] = 128
let test6 = bin_to_dec [1] = 1
let test7 = bin_to_dec [0] = 0

let _ =
  print_endline (string_of_bool test1);
  print_endline (string_of_bool test2);
  print_endline (string_of_bool test3);
  print_endline (string_of_bool test4);
  print_endline (string_of_bool test5);
  print_endline (string_of_bool test6);
  print_endline (string_of_bool test7);