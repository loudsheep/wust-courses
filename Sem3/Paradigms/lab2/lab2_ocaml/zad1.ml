let rec divide arr =
  if arr = [] then ([], [])
  else
    let h::t = arr in
    let (odd, even) = divide t in

    if h mod 2 = 0 then (odd, h :: even)
    else (h :: odd, even);;


let () =
  print_endline ("divide [1,2,3,4,5,6] = ([1,3,5],[2,4,6]) : " ^ (string_of_bool (divide [1;2;3;4;5;6] = ([1;3;5],[2;4;6]))));
  print_endline ("divide [] = ([],[]) : " ^ (string_of_bool (divide [] = ([],[]))));
  print_endline ("divide [2,4,6,8] = ([],[2,4,6,8]) : " ^ (string_of_bool (divide [2;4;6;8] = ([],[2;4;6;8]))));
  print_endline ("divide [1,3,5,7] = ([1,3,5,7],[]) : " ^ (string_of_bool (divide [1;3;5;7] = ([1;3;5;7],[]))));
  print_endline ("divide [1,2,3,4] = ([1,3],[2,4]) : " ^ (string_of_bool (divide [1;2;3;4] = ([1;3],[2;4]))));
;;