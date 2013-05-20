;; * make a copy of source folder
;; * get md5sum of all files
;; * compare md5sum of every file, note
;;  * deleted file
;;  * added file
;;  * empty directory
;;  * ignored files/directories

(ns GeneralTestSuite.foresync
  (use clojure.java.io)
  ;(import java.security.MessageDigest)
  (require digest)
  (:use [GeneralTestSuite.util :only [write-log]])
  (:import (java.util Date)
           (java.text SimpleDateFormat))
)

(def timeformat "yyyy-MM-dd'T'HH-mm-ss.SSSZ")

(defn list-dir 
  "List all files under a directory, 
  returns a list of java.io.File.
  Unlike file-seq, this function oes not walk into sub-directories.
   Deprecated, use clojure.core.file-seq"
  [file-loc]
  (let [f (as-file file-loc)] 
    (cond (.isFile f)   (list f)
      (.isDirectory f) (.listFiles f))))

(defn print-file-list [file-list]
  (println "name\t\t size\t\t md5sum")
  (doseq [f file-list]
    (println (str (.getName f) "\t\t" 
                  (when (.isFile f) (str  (.length f) "\t\t" (digest/md5 f)))
                  (when (.isDirectory f) "dir\t\t")))))

(defn get-md5 
  "Calculate md5sum of a file."
  [file-path]
  (digest/md5 (as-file file-path)))

;; Data structure
;; ({:name :size :md5} {:name :size :md5} ... )
(defn list-files*
  "Get a sequence of file info.
   Not a lazy-implementation, DANGER!!"
  [file-list]
  (loop [new-file-list file-list result-list '()]
    (if (empty? new-file-list)
      result-list
      (let [f (first new-file-list)
          name (.getName f)
          size (if (.isDirectory f) nil 
                 (.length f))
          md5sum (if (.isDirectory f) nil
                   (digest/md5 f))]
        (recur (rest new-file-list) (cons {:name name :size size :md5sum md5sum} result-list))))))

(defn get-file-info*
  "Get file information of a file. Potentially time consuming."
  [f]
  ;TODO Should filter ignored file/dir 
  (let [name (.getName f)
        size (if (.isDirectory f) nil 
                 (.length f))
        md5sum (if (.isDirectory f) nil
                   (digest/md5 f)) ]
    {:name name :size size :md5sum md5sum}))

(defn get-file-info
  [f]
  (future (get-file-info* f)))

(defn list-files-concurr*
  [file-list]
  (loop [new-file-list file-list
         result-list '()]
    (if (empty? new-file-list) 
        result-list
        (recur (rest new-file-list)
               (cons (get-file-info (first new-file-list))
                     result-list)))))

(defn list-files-concurr
  "List file information concurrently. 
  Usage: (def future-files (doall (list-files-concurr file-list)))
  Note use doall to realize the map operation immediately.
  Need to deref to get actual values."
  [file-list]
  (map get-file-info file-list))


(defn list-files 
  "Get a sequence of file info in the specified directory"
  [dir]
  (list-files* (file-seq (as-file dir))))

(defn get-curr-date-time* 
  "Get current date-time as string"
  []
  (let [sdf (SimpleDateFormat. timeformat)] 
                (.format sdf (Date.))))

(defn file-name-comparator
  [first-info-map second-info-map]
  (let [first-name (clojure.string/lower-case (:name first-info-map))
        second-name (clojure.string/lower-case (:name second-info-map))]
    (compare first-name second-name)))

(defn sort-info-by-name
  [file-info-seq]
  (sort file-name-comparator file-info-seq))

(defn write-dirinfo-log
  "Write dir information to log"
  [dir ver]
  (let [msg (list-files dir)
        log (str ver "-" (get-curr-date-time*) ".log")]
    (write-log msg log true)))

