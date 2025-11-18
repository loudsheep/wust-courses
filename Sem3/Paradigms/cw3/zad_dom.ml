(* z cukerm syntaktycznym *)
let curry4 f x y z w = f (x, y, z, w)
let uncurry4 f (x, y, z, w) = f x y z w

(* bez cukru *)
let curry4_ns = fun f -> fun x -> fun y -> fun z -> fun w -> f (x, y, z, w)
let uncurry4_ns = fun f -> fun (x, y, z, w) -> f x y z w
