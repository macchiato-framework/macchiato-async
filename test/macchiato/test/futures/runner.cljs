(ns macchiato.test.futures.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [macchiato.test.futures.core]))

(doo-tests 'macchiato.test.futures.core)