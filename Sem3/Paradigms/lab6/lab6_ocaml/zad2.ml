type 'a tree =
  | Empty
  | Node of 'a * 'a tree * 'a tree

let rec tree_stats (t: int tree) : int * int =
  match t with
  | Empty -> (0, 0)
  | Node (_, left, right) ->
      let (left_nodes, left_height) = tree_stats left in
      let (right_nodes, right_height) = tree_stats right in

      let total_nodes = 1 + left_nodes + right_nodes in
      let height = 1 + max left_height right_height in
      (total_nodes, height)



let t1 = Empty
let t2 = Node(1, Empty, Empty)
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
  let test_trees = [t1; t2; t3; t4; t5] in
  List.iter (fun t ->
    let (nodes, height) = tree_stats t in
    Printf.printf "Nodes: %d, Height: %d\n" nodes height
  ) test_trees