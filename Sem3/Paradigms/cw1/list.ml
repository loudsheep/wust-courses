(* 1 *)
let rec flatten1 (xss : 'a list list) : 'a list =
  if xss = [] then []
  else List.hd xss @ flatten1 (List.tl xss)

(* 2 *)
let rec count (x : 'a) (xs : 'a list) : int =
  if xs = [] then 0
  else if List.hd xs = x then 1 + count x (List.tl xs)
  else count x (List.tl xs)

(* 3 *)
let rec replicate (x : 'a) (n : int) : 'a list =
  if n <= 0 then []
  else [x] @ replicate x (n - 1)

(* 4 *)
let rec sqrList (xs : int list) : int list =
  if xs = [] then []
  else [ (List.hd xs) * (List.hd xs) ] @ sqrList (List.tl xs)

(* 5 *)
let palindrome (xs : 'a list) : bool =
  xs = List.rev xs

(* 6 *)
let rec listLength (xs : 'a list) : int =
  if xs = [] then 0
  else 1 + listLength (List.tl xs)