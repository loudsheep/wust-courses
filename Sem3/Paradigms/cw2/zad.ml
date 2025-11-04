(* 2 *)
let rec fib(x : int) : int =
    match x with
    | 0 -> 0
    | 1 -> 1
    | _ -> fib(x - 1) + fib(x - 2)

let fibTail(x : int) : int =
    let rec addNums (x1 : int) (x2: int) (i : int) = 
        if i = 0 then x1 else addNums (x1 + x2) x1 (i - 1)
    in
    if x <= 0 then 0 
    else if x <= 2 then 1 
    else addNums 1 1 (x-2)

(* 3 *)
let root3 (a : float) : float =
  let rec iterate (x : float) : float =
    if abs_float (x *. x *. x -. a) <= 1e-15 then x
    else iterate (x +. (a /. (x *. x) -. x) /. 3.)
  in
  if a = 0. then 0.
  else
    let x0 = if a > 1. then a /. 3. else a in
    iterate x0

(* 4 *)
let [_; _; x; _; _] = [-2; -1; 0; 1; 2];;
let [(_, _); (x, _)] = [(1,2); (0,1)];;

(* 5 *)
let rec initSegment (xs : 'a list) (xss : 'a list) : bool =
  match (xs, xss) with
  | ([], _) -> true                  
  | (_, []) -> false                 
  | (x::xt, y::yt) ->
      if x = y then initSegment xt yt
      else false

(* 6 *)
let rec replaceNth (xs: 'a list) (n : int) (x: 'a) : 'a list =
  match (xs, n) with
  | ([], _) -> []
  | (_ :: tail, 0) -> x :: tail
  | (head :: tail, k) -> head :: replaceNth(tail)( k-1)(x)