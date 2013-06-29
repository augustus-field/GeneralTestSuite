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

(defn log [msg]
  (swap! logger
         (fn [oldlog]
           (let [newlog (doall (take max-log-entries (cons msg oldlog)))]
             (with-open [wr  log-file]
               (binding [*out* wr]
                 (print newlog)
                 newlog))))))

; factorial
(defn fact
  "Calculate factorial without optimization"
  [n]
  (if (< n 1)
    1
    (* n (fact (dec n)))))


(defn fib
  "Generate a lazy fibonacci sequence."
  ([] (fib 0 1))
  ([a b] (cons a (lazy-seq (fib b (+ a b))))))

(defn range-check [min max]
  (fn [num]
    (and (> num min)
         (< num max))))

(defn indexed
  [coll]
  (map-indexed vector coll))

(defn index-filter
"Similar to indexOfAny method of StringUtils in Java"
  [pred coll]
  (when pred
    (for [[idx elt] (indexed coll) :when (pred elt)]
      idx)))

(defn trmcoll
  [pred coll]
  (take-while pred (indexed coll)))

(defn trmstr
  [idx str]
  (let [rs (trmcoll #(< (first %) idx)
           (indexed str))]
    (apply str (map second rs))))

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

(defn min-max-loop [one & more]
  (loop [min one
         max one
         [one & more]  more]
    (if one
      (recur (if (< one min) one min)
             (if (> one max) one max)
             more)
      (assoc {} :min min :max max))))

;; merge-with takes a function and multiple maps. It merges 
;; these maps by applying the function continousely 
(defn min-max [one & more]
  (reduce (fn [result x] (->> result
                              (merge-with min {:min x})
                              (merge-with max {:max x})))
          {:min one :max one}
          more))
;; Note difference between -> and ->>
;; -> inserts the first expression x as the second item in the first form, while
;; ->> inserts the first expression x as the last item in the first form


(defn zipm-1 [keys vals]
  (loop [m {}
         ks (seq keys)
         vs (seq vals)]
    (if (and ks vs)
      (recur (assoc m (first ks) (first vs))
             (next ks)
             (next vs))
      m)))

(defn zipm-2 [keys vals]
  (loop [m {}
         [k & more-ks :as keys] (seq keys)
         [v & more-vs :as vals] (seq vals)]
    (if (and keys vals)
      (recur (assoc m k v)
             more-ks
             more-vs)
      m)))

(defn zipm-3 [keys vals]
  (reduce (fn [m [k v]] (assoc m k v))
          {}
          (map vector keys vals)))

(defn zipm-4 [keys vals]
  (apply hash-map (interleave keys vals)))

(defn zipm-5 [keys vals]
  (into {} (map vector keys vals)))
