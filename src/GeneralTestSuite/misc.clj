(ns GeneralTestSuite.misc)

;; Misc ways for creating maps from key and value sequences

(defn map-reduce 
  [keys vals]
  (reduce (fn [m [k v]]
            (assoc m k v))
          {}
          (map vector keys vals)))


(defn map-interleave
  [keys vals]
  (apply hash-map (interleave keys vals)))


(defn map-into
  [keys vals]
  (into {} (map vector keys vals)))

(defn map-zipmap
  [keys vals]
  (zipmap keys vals))

; cond-> like cond, but using threads and does not short-circuit
(defn testobj [obj] (cond-> obj (not (= "1" (:attr obj))) (assoc :attr "assigned ") (not (:info obj) ) (println "not equal") ) )


; filter to show all maps with value of :a equals nil from a list of maps
(def m [{:a 1 :b nil} {:a 2 :b 3} {:a nil :b 4}])
(filter (comp (complement nil?) :a) m)
; or even simpler
(filter (complement :a) m)

; a logger example 
(def logger (atom nil))
(def max-log-entries 100)
(def log-file (clojure.java.io/as-file "samplelog.log"))
;; (defn log [msg]
;;   (swap! logger
;;          (fn [oldlog]
;;            (let [newlog (doall (take max-log-entries (cons msg oldlog)))]
;;              (with-open [wr writer-on log-file]
;;                (binding [*out* wr]
;;                  (print newlog)
;;                  newlog))))))

; Create a vector of (possibly infinite) length
(reduce #(conj %1 %2) []  (range 10) )

; merge two list of maps
(def xs [{:a 1 :b 2 :d 6} {:a 2 :b 2} {:a 7 :b 7}])
(def ys  [{:a 3 :c 3 :e 9 :y 7} {:a 2 :b 6 :c 8}])
(for [[a ms] (group-by :a (concat xs ys))] (apply merge ms))

; fib
(defn lazy-fib []
  (map first (iterate (fn [ [a b] ] [b (+ a b) ]) [0N 1N])))

(defn min-1 [x & more]
  (loop [min x
         more (seq more)]
    (if-let [i (first more)]
      (recur (if (< i min) i min) (next more))
      min)))

(defn min-2 [x & more]
  (loop [min x
         [i & more] (seq more)]
    (if i
      (recur (if (< i min) i min) more)
      min)))

