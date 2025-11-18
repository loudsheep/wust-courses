(* ZADANIE 2 *)
(* Z lukrem syntaktycznym *)
let curry3 f x y z = f (x, y, z)
let uncurry3 f (x, y, z) = f x y z

(* Bez lukru syntaktycznego *)
let curry3_ns = fun f -> fun x -> fun y -> fun z -> f (x, y, z)
let uncurry3_ns = fun f -> fun t -> 
  match t with (x, y, z) -> f x y z


(* ZADANIE 3 *)
let sumProd xs =
  List.fold_left (fun (s, p) x -> (s + x, p * x)) (0, 1) xs


(* ZADANIE 4 *)
(* Nieskończona rekurencja, bo pivot jest dodawany do 'large' *)


(* ZADANIE 5 *)
let rec insertionsort pred xs =
  let rec insert x sorted_list =
    match sorted_list with
    | [] -> [x]
    | h :: t -> 
        (* Jeśli x jest ściśle mniejszy od h, wstaw przed h.
           Jeśli x >= h, idź dalej (zachowuje stabilność). *)
        if pred x h && not (pred h x) then x :: h :: t
        else h :: (insert x t)
  in
  List.fold_left (fun acc x -> insert x acc) [] xs


let rec mergesort pred xs =
  let split list =
    let n = List.length list in
    let rec take n lst = 
        if n = 0 then [] else match lst with h::t -> h :: take (n-1) t | [] -> [] in
    let rec drop n lst = 
        if n = 0 then lst else match lst with _::t -> drop (n-1) t | [] -> [] in
    (take (n / 2) list, drop (n / 2) list)
  in
  let rec merge left right =
    match (left, right) with
    | ([], r) -> r
    | (l, []) -> l
    | (lh :: lt, rh :: rt) ->
        (* Stabilność: jeśli lh <= rh, wybierz lh (z lewej listy) *)
        if pred lh rh then lh :: merge lt right
        else rh :: merge left rt
  in
  match xs with
  | [] -> []
  | [x] -> [x]
  | _ ->
      let (l, r) = split xs in
      merge (mergesort pred l) (mergesort pred r)