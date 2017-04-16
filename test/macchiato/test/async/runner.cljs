(ns macchiato.test.async.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [macchiato.test.async.common]
            [macchiato.test.async.futures]))

(doo-tests 'macchiato.test.async.common
           'macchiato.test.async.futures)