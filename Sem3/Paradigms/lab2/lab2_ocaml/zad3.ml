let rec merge arr1 arr2 = 
    if arr1 = [] && arr2 = [] then []
    else if arr1 = [] then List.hd arr2 :: merge arr1 (List.tl arr2)
    else if arr2 = [] then List.hd arr1 :: merge (List.tl arr1) arr2
    else
      List.hd arr1 :: List.hd arr2 :: merge (List.tl arr1) (List.tl arr2);;


let () =
  print_endline (String.concat "," (List.map string_of_int (merge [1;3;5] [2;4;6])));
  print_endline (String.concat "," (List.map string_of_int (merge [] [2;4;6])));
  print_endline (String.concat "," (List.map string_of_int (merge [1;3;5] [])));
  print_endline (String.concat "," (List.map string_of_int (merge [] [])));
  print_endline (String.concat "," (List.map string_of_int (merge [1;2;3] [4;5])));
  print_endline (String.concat "," (List.map string_of_int (merge [1;2;3;4;5;6;7;8] [9;10;11])));
;;