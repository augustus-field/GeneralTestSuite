(ns GeneralTestSuite.foresync-test
  (:use clojure.test
        clojure.pprint
        GeneralTestSuite.foresync))

;; Sort files by name
(defn sorted-file-info
  [dir]
  (sort-info-by-name
          (sort-info-by-name
    (list-files dir))))

;; Copy a working folder to a location as archive files and a temp-working location
(defn make-file-copy
  [dir]
  (future (copy-files dir)))

(defn compare-file
  "Two files are identical only when they have 
 - identical checksum value 
 - same relative location to their respective root 
Mark files that needs to be 
 - added
 - updated (effectively equiv. to the previous case) 
 - removed
Data structure:
\"{:name :location :size :md5sum :utag}" 
  [old-file-info new-file-info]
  ))


(defn get-diff-file-seq
"Compare temp-working files with files of previous version,
and generate a diff logging the difference" 
  [old-file-info-list new-file-info-list]
;;Compare files
  )

(defn update-files
"Perform the add/remove/update action based on the value of :utag 
of every file-dff map "
  [files-diff-seq]
  )
