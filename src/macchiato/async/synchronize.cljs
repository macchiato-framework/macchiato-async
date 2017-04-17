(ns macchiato.async.synchronize
  (:require [cljs.nodejs :as node]))

(def Fiber (node/require "fibers"))

(def await (.-yield Fiber))

(defn defer-serial
  "Returns a callback which will defer execution serially on the current fiber."
  []
  (if-let [fiber (.-current Fiber)]
    (fn [err result]
      (when-not (.-_syncIsTerminated fiber)
        (if err
          (.throwInto fiber err)
          (.run fiber result))
        ))
    (throw (js/Error "no current Fiber, defer can't be used without Fiber!"))))

(def defer defer-serial)


(defn synchronize
  "If called with a single parameter that is a function, it'll return a
  synchronized version of a function, which will call await and use defer
  as the callback.

  When called with an object and a list of function names, it'll create
  new synchronized versions of the functions in the object for each of those
  functions being called."
  ([the-fn]
   (if (aget the-fn :synchronized?)
     (aget the-fn :sync-fn)
     (if (= (type the-fn) js/Function)
       (let [synced (fn [& args]
                      (await (apply the-fn (concat args [(defer)]))))]
         (aset the-fn :synchronized? true)
         (aset the-fn :sync-fn synced)
         synced)
       (throw (js/Error "Expected a function, got " (type the-fn))))))
  ([the-obj & fn-names]
   (doseq [name fn-names]
     (let [the-fn (aget the-obj name)
           synced (synchronize the-fn)]
       (aset the-obj (str name "Sync") synced)))
   the-obj))