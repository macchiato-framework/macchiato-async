(ns macchiato.futures.futures)

(defmacro in
  "Runs the exprs in a future
   args: [& exprs]"
  [& exprs]
  `(call (fn [] ~@exprs)))
