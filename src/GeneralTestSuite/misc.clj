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


;; JoC EXAMPLE

(defmacro get-available-method-names
         "Get the public method names of a class"
         [class-name]
         `(for [method# (.getMethods ~class-name)
               :let [method-name# (.getName method#)]]
           (prn method-name#)))

(def frame (java.awt.Frame.))
(.setSize frame (java.awt.Dimension. 200 200))
(.setVisible frame true)
(def gfx (.getGraphics frame))
(prn (nil? gfx))
(.fillRect gfx 100 100 50 50)
(.setColor gfx (java.awt.Color.  255 182 0))
(.fillRect gfx 150 50 75 50)

(defn xors
  "Create a seq of vectors of [x y x-bit-xor-y]"
  [x-max y-max]
  (for [x (range x-max) y (range y-max)]
    [x y (bit-xor x y)]))

(doseq [ [x y z] (xors 200 200) ]
  (.setColor gfx (java.awt.Color. z z z ))
  (.fillRect gfx x y 1 1))

(defn clear-graphics
  "Clear graphics"
  [g]
  (.clearRect g 0 0 200 200))

(defn f-values
  "Similar to xors, but use supplied function instead of bit-xor"
  [f x-max y-max]
  (for [x (range x-max)
        y (range y-max)]
      [x y (rem (f x y) 256)]))

(defn draw-values
  [f x-max y-max]
  (clear-graphics gfx)
  (.setSize frame (java.awt.Dimension. x-max y-max))
  (doseq [[x y z] (f-values f x-max y-max)]
    (.setColor gfx (java.awt.Color. z z z))
    (.fillRect gfx x y 1 1)))

(re-seq #"(\w)+\s+(\w+)" "searc in,context and ,convert to vectors")

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

;;; Project euler
(defn divides? [dividend divisor]
  (zero? (rem dividend divisor)))

(defn divides-all?
"Test if all divisors can evenly divide dividends."
  [dividend  & divisors]
  (every? (partial divides? dividend) divisors))

(defn divides-any?
  "Returns a function that tests whether its arg can be evenly divided by
any of nums."
  [& nums]
  (fn [arg] (boolean (some
                       #(divides? arg %)
                       nums))))

;; Problem 1
;; Multiples of 3 and 5
;; If we list all the natural numbers below 10 that are multiples of 3 or 5, we get 3, 5, 6 and 9. The sum of these multiples is 23.
;; Find the sum of all the multiples of 3 or 5 below 1000.
(defn problem-1-solv
  ([limit]
     (loop [sum 0
         n 1]
    (if (< n limit)
      (recur (if ((divides-any? 3 5) n)
               (+ sum n) sum)
             (inc n))
      sum)))
  ([] (problem-1-solv 10)))

(defn problem-1-solv-by-filter
  ([limit]
     (apply + (filter (divides-any? 3 5) (range 1 limit))))
  ([] (problem-1-solv-by-sum 10)))

(defn problem-1-solv-by-threading
  ([limit]
     (->> (range 1 limit)
          (filter (divides-any? 3 5))
          (apply +)))
  ([] (problem-1-solv-by-sum 10)))
