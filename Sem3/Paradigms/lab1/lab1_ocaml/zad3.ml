let rec suma arr = 
    if arr = [] then 0.
    else List.hd arr +. suma (List.tl arr);;


let () =
  print_endline (string_of_float(suma [6.;4.;2.]));
;;