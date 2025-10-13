let rec sum arr = 
    if arr = [] then 0.
    else List.hd arr +. sum (List.tl arr);;


let () =
  print_endline (string_of_float(sum [6.;4.;2.]));
  print_endline (string_of_float(sum []));
  print_endline (string_of_float(sum [-6.;-5.;0.;0.;0.;0.;0.;0.;0.;0.]));
  print_endline (string_of_float(sum [-9.;9.]));
;;