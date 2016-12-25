(ns macchiato.futures.core
  (:require [macchiato.futures.fut :as future]))

;; Compatibility shim

(def ^{:deprecated "0.7"} Future
  "DEPRECATED: use `macchiato.futures.future/Future` instead"
  future/Future)

(def ^{:deprecated "0.7"} wrap-future
  "DEPRECATED: use `macchiato.futures.future/wrap` instead"
  future/wrap)

(def ^{:deprecated "0.7"} task
  "DEPRECATED: use `macchiato.futures.future/call` instead"
  future/call)

(def ^{:deprecated "0.7"} detached-task
  "DEPRECATED. Alternatives:
  `macchiato.futures/fire-and-forget`
  `macchiato.futures.future/call-forget`"
  future/call-forget)

(def ^{:deprecated "0.7"} wait
  "DEPRECATED: use `deref` instead"
  deref)

