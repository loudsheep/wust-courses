type graph = (int * int list) list

let rec contains (elem: int) (lst: int list) : bool =
  match lst with
  | [] -> false
  | h :: t -> if h = elem then true else contains elem t

let rec get_neighbors (g: graph) (node: int) : int list =
  match g with
  | [] -> []
  | (n, neighbors) :: t ->
      if n = node then neighbors
      else get_neighbors t node


let reachable_count (g: graph) (start: int) : int =
  let rec dfs visited stack =
    match stack with
    | [] -> visited
    | h :: t ->
        if contains h visited then
          dfs visited t
        else
          let neighbors = get_neighbors g h in
          dfs (h :: visited) (neighbors @ t)
  in
  if get_neighbors g start = [] then
    0
  else
    let visited_nodes = dfs [] [start] in
    List.fold_left (fun acc _ -> acc + 1) 0 visited_nodes


let g_example = [
  (1, [2;3]);
  (2, [4]);
  (3, [4;5]);
  (4, []);
  (5, [4])
]

let g_cycle = [
  (1, [2]);
  (2, [3]);
  (3, [1])
]

let g_linear = [
  (1, [2]);
  (2, [3]);
  (3, [4]);
  (4, [])
]

let g_empty = []

let test1 = reachable_count g_example 1
let test2 = reachable_count g_example 2
let test3 = reachable_count g_cycle 1
let test4 = reachable_count g_linear 2
let test5 = reachable_count g_empty 1

let () =
  Printf.printf "Test 1: %d\n" test1;
  Printf.printf "Test 2: %d\n" test2;
  Printf.printf "Test 3: %d\n" test3;
  Printf.printf "Test 4: %d\n" test4;
  Printf.printf "Test 5: %d\n" test5