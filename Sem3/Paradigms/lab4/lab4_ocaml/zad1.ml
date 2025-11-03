
let sum arr =
  List.fold_left (fun acc x -> acc + x) 0 arr

let f_l arr target_sum = 
  List.filter (fun x -> sum x = target_sum) arr


let test1 = f_l [[1;2;3]; [2;4]; [5;6]] 6 = [[1;2;3]; [2;4]]
let test2 = f_l [[1;1;1]; [3;0;0]; [2;2;2]] 3 = [[1;1;1]; [3;0;0]]
let test3 = f_l [[]; [0]; [1]] 0 = [[]; [0]]
let test4 = f_l [[10]; [5;5]; [3;7]] 10 = [[10]; [5;5]; [3;7]]
let test5 = f_l [[1;2]; [2;3]; [3;4]] 5 = [[2;3]]
let test6 = f_l [] 0 = []

let _ =
  print_endline (string_of_bool test1);
  print_endline (string_of_bool test2);
  print_endline (string_of_bool test3);
  print_endline (string_of_bool test4);
  print_endline (string_of_bool test5);
  print_endline (string_of_bool test6);