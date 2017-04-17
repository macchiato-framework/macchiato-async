(ns macchiato.async.synchronize
  (:require [cljs.nodejs :as node]))

(def Fiber (node/require "fibers"))

(def await (.-yield Fiber))

(defn defer-serial []
  (if-let [fiber (.-current Fiber)]
    (fn [err result]
      (when-not (.-_syncIsTerminated fiber)
        (if err
          (.throwInto fiber err)
          (.run fiber result))
        )
      )
    (throw (js/Error "no current Fiber, defer can't be used without Fiber!"))))

(def defer defer-serial)