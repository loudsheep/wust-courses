let rec mniejsze arr num = 
    if arr = [] then true
    else if List.hd arr < num then mniejsze (List.tl arr) num
    else false;;


let () =
  print_endline (string_of_bool(mniejsze [6;4;2] 7));
  print_endline (string_of_bool(mniejsze [1;2;3;4;5;6;7;8] 7));
  print_endline (string_of_bool(mniejsze [] 5));
  print_endline (string_of_bool(mniejsze [-3;-4;-5] 5));
;;