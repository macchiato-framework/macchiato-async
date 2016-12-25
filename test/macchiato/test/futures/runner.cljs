(ns macchiato.test.futures.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [macchiato.test.futures.tests]))

(doo-tests 'macchiato.test.futures.tests)
