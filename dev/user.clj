(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.repl :refer :all]
            [clojure.pprint :refer [pp pprint cl-format]]
            [cls.defunits :refer :all]))



(defn make-unit-table [units]
  (loop [table {}
         [[sym & df] & rest-units] units]
    (if df
      (recur
        (assoc table
               sym
               df)
        rest-units)
      table)))


(defn build [table base-unit value unit]
  (if (= base-unit unit)
    value
    (if-let [[d-value d-unit] (get table unit)]
      `(* ~value
          ~(build table
                  base-unit
                  d-value
                  d-unit)))))


(defmacro defunits [quantity base-unit & units]
  `(defmacro ~(symbol (str 'unit-of-
                           quantity))
     [value# unit#]
     (let [table# (make-unit-table (partition 3 '~units))]
       (eval (build table#
                    '~base-unit
                    value#
                    unit#)))))

; (defunits time s m 60 s h 60 m d 24 h w 7 d)

(pprint
  (macroexpand-1
    '(defunits time s
       m 60 s
       h 60 m
       d 24 h
       w 7 d)))
