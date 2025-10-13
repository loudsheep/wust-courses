let trzeci (a,b,c) = c;;

let () =
  print_endline (trzeci (6, 4., "ala"));
  print_endline (string_of_int(trzeci (6, 4, 1)));
  print_endline (string_of_float(trzeci (6, 4, -3.)));
;;