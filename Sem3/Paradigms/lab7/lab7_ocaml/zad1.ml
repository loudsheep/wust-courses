type 'a llist = LNil | LCons of 'a * (unit -> 'a llist)

let rec toLazyList arr =
  match arr with
  | [] -> LNil
  | h :: t -> LCons (h, function () -> toLazyList t)
  

let rec lfrom k = LCons (k, function () -> lfrom (k+1))

let rec ltake (n, arr) =
  match (n, arr) with
  | (0, _) -> []
  | (_, LNil) -> []
  | (n, LCons (h, t)) -> h :: ltake (n - 1, t ())

let rec lwybierz n m arr =
  match arr with
  | LNil -> LNil
  | LCons (h, t) ->
      if n <= 0 || m <= 0 then
        LNil
      else if m = 1 then
        LCons(h, function () -> lwybierz n n (t()))
      else
        lwybierz n (m - 1) (t ())

(* testy *)
let lista1 = toLazyList [5; 6; 3; 2; 1];;
let wynik1 = lwybierz 2 1 lista1;;
let test1 = ltake (5, wynik1) = [5; 3; 1];;
print_endline (string_of_bool test1);;

let lista2 = lfrom 1;;
let wynik2 = lwybierz 3 2 lista2;;
let test2 = ltake (6, wynik2) = [2; 5; 8; 11; 14; 17];;
print_endline (string_of_bool test2);;

let lista3 = toLazyList [1; 2];;
let wynik3 = lwybierz 2 5 lista3;;
let test3 = ltake (5, wynik3) = [];;
print_endline (string_of_bool test3);;

let lista4 = toLazyList [10; 20; 30; 40; 50];;
let wynik4 = lwybierz 1 3 lista4;;
let test4 = ltake (5, wynik4) = [30; 40; 50];;
print_endline (string_of_bool test4);;

let lista5 = toLazyList [];;
let wynik5 = lwybierz 2 1 lista5;;
let test5 = ltake (5, wynik5) = [];;
print_endline (string_of_bool test5);;

let lista6 = lfrom 10;;
let wynik6 = lwybierz 0 1 lista6;;
let test6 = ltake (5, wynik6) = [];;
print_endline (string_of_bool test6);;