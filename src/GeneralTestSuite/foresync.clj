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

(defn list-files 
  "Get a sequence of file info in the specified directory"
  [dir]
  (list-files* (file-seq (as-file dir))))

(defn get-curr-date-time* 
  "Get current date-time as string"
  []
  (let [sdf (SimpleDateFormat. timeformat)] 
                (.format sdf (Date.))))

(defn write-dirinfo-log
  "Write dir information to log"
  [dir ver]
  (let [msg (list-files dir)
        log (str ver "-" (get-curr-date-time*) ".log")]
    (write-log msg log true)))

