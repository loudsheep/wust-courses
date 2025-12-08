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


let rec lazyFibN n =
  let rec genFib a b count =
    if count >= n || a < 0 then LNil
    else LCons (a, function () -> genFib b (a + b) (count + 1))
  in
  genFib 0 1 0


(* Test lazyFibN *)
let test1 = ltake (100, lazyFibN 9);;
let res1 = (test1 = [0; 1; 1; 2; 3; 5; 8; 13; 21]);;
print_endline (string_of_bool res1);;

let test2 = ltake (100, lazyFibN 14);;
let res2 = (test2 = [0; 1; 1; 2; 3; 5; 8; 13; 21; 34; 55; 89; 144; 233]);;
print_endline (string_of_bool res2);;

let test3 = ltake (100, lazyFibN 1);;
let res3 = (test3 = [0]);;
print_endline (string_of_bool res3);;

let test4 = ltake (100, lazyFibN 0);;
let res4 = (test4 = []);;
print_endline (string_of_bool res4);;

let test5 = ltake (100, lazyFibN (-100));;
let res5 = (test5 = []);;
print_endline (string_of_bool res5);;