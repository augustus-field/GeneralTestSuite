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