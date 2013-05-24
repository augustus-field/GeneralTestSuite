;; Use result from Linux rsync trial run to generate patch.
;; Linux command: 
;;   rsync -vrnc $sourcepath $archivepath > /dutch/Versions/$month/diff-${fulldate}.log
(ns GeneralTestSuite.rsync)

