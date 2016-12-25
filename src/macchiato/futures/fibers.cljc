(ns macchiato.futures.fibers)

(defmacro in
  "Runs body exprs inside a new fiber
   args: [& exprs]"
  [& exprs]
  `(run-in (fn [] ~@exprs)))

