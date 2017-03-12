(ns macchiato.test.async.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [macchiato.test.async.core]))

(doo-tests 'macchiato.test.async.core)