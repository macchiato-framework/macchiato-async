(ns macchiato.futures.fibers)

(defmacro as
  "Turns body exprs into a new Fiber object
   args: [& exprs]
   returns: Fiber"
  [& exprs]
  `(make (fn [] ~@exprs)))

(defmacro in
  "Runs body exprs inside a new fiber
   args: [& exprs]"
  [& exprs]
  `(run-in (fn [] ~@exprs)))

