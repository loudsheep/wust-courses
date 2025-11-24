type 'a tree =
  | Empty
  | Node of 'a * 'a tree * 'a tree

let rec tree_stats (t: 'a tree) : int * int =
  let rec aux (t: 'a tree) (nodes: int) (height: int): int * int =
    match t with
    | Empty -> (nodes, height)
    | Node (_, left, right) ->
        let (left_nodes, left_height) = aux left (nodes + 1) (height + 1) in
        let (right_nodes, right_height) = aux right left_nodes (height + 1) in
        (right_nodes, max left_height right_height)
  in
  aux t 0 0


let t1 = Empty
let t2 = Node("de", Empty, Empty)
let t3 = Node(1, Node(2, Node(2, Node(2, Node(2, Empty, Empty), Empty), Node(2, Empty, Empty)), Empty), Node(2, Empty, Empty))
let t4 = Node(1,
  Node(2,
    Node(3, Empty, Empty),
    Empty
  ),
  Node(4, Empty, Empty)
)
let t5 = Node(10,
  Node(5, Empty, Empty),
  Node(15,
    Node(12, Empty, Empty),
    Node(20, Empty, Empty)
  )
)

let () =
  let (n1, h1) = tree_stats t1 in
  Printf.printf "Nodes: %d, Height: %d\n" n1 h1;

  let (n2, h2) = tree_stats t2 in
  Printf.printf "Nodes: %d, Height: %d\n" n2 h2;

  let (n3, h3) = tree_stats t3 in
  Printf.printf "Nodes: %d, Height: %d\n" n3 h3;

  let (n4, h4) = tree_stats t4 in
  Printf.printf "Nodes: %d, Height: %d\n" n4 h4;

  let (n5, h5) = tree_stats t5 in
  Printf.printf "Nodes: %d, Height: %d\n" n5 h5