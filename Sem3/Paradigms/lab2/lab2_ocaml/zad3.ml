let rec merge arr1 arr2 = 
    match arr1, arr2 with
    | [], [] -> []
    | [], h1::t1 -> h1 :: merge [] t1
    | h2::t2, [] -> h2 :: merge t2 []
    | h1::t1, h2::t2 -> h1 :: h2 :: merge t1 t2;;

let () =
  print_endline (String.concat "," (List.map string_of_int (merge [1;3;5] [2;4;6])));
  print_endline (String.concat "," (List.map string_of_int (merge [] [2;4;6])));
  print_endline (String.concat "," (List.map string_of_int (merge [1;3;5] [])));
  print_endline (String.concat "," (List.map string_of_int (merge [] [])));
  print_endline (String.concat "," (List.map string_of_int (merge [1;2;3] [4;5])));
  print_endline (String.concat "," (List.map string_of_int (merge [1;2;3;4;5;6;7;8] [9;10;11])));
;;