let rec mniejsze arr num = 
    if arr = [] then true
    else if List.hd arr < num then mniejsze (List.tl arr) num
    else false;;


let () =
  print_endline (string_of_bool(mniejsze [6;4;2] 7));
;;