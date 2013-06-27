(ns GeneralTestSuite.util
   (:require [clj-http.client :as client])
   (:require [clojure.data.json :as json]))



(defn get-time
  "Get time in millis at a number of hours ago"
  [num-of-hours]
  (- (System/currentTimeMillis) (* 3600 1000 num-of-hours)))
(defn get-time-one-week-ago
  []
  (get-time (* 6 24)))
(defn get-time-one-day-ago
  []
  (get-time (* 23 1)))

;; http-REQUEST related
;; cookie store: for cross-requests maintenance of cookies
(defn get-cookie [] (clj-http.cookies/cookie-store))
(def ^:dynamic cookie (get-cookie))

(defn request-get
  "Send http get request, return the raw result. 
   Need to bind a cookie specific for the calling thread."
  [url]
  (client/get url  {:cookie-store cookie}))

(def  result-code-set
  "A set saving all result codes. 
  TODO Threads should have their own copies of result code set."
  (atom  #{}))

;; json-parsing
(defn error-inspector 
  "Check for error code in return json values. Print error to standard output.
   This function returns original value of the key."
  [key value]
  ;(println (str key ":" value))
    (if (and (= key "return_code")
             (or (= value 59)
                 (= value "59")))
      (println "Error detected!--->" value ". ")
      (if (and (= key "result")
               (and (not= value 0)
                   (not= value "0")))
        (do ;(print "Warning!--->" value ". ")
          (swap! result-code-set conj value))))
  value)

(defn check-return-code [{rc "return_code"}]
  "Check return_code value from a map."
  (if (or (= rc 59)
          (= rc "59"))
    "Error detected!\n"
    nil)
  )

(defn process-jmap [jmap]
  (check-return-code jmap)
  )


(defn read-json-str [json-str] 
  "Read a json string and print any error if it exists. 
   always returns original string."
  ;(println str)
  (str (let [jmap (json/read-str json-str :value-fn error-inspector)]
         ;(println (str "Processing json-map:\n" jmap))
         (process-jmap jmap))
       json-str))


(defn write-log
  "Writes the string msg to log file"
  [msg logfile suppress-console]
  (if-not suppress-console (println msg))
  (with-open [w (clojure.java.io/writer logfile :append true)]
    (.write w (str msg "\n"))))


(defn wrap-write-log
  "Write output to location using options configured in login-map"
  [output login-map]
  (write-log output
              (:log-location login-map)
	             (:suppress-console login-map)))




(defn clean-log
  "Removes log file, exception will be ignored" 
  [filename]
;	 (println "Removing log: " filename "if exists.")
	  (try 
	    (clojure.java.io/delete-file filename)
	    (catch Exception e)))




(defn exit 
  "Exit REPL"
  []
  (System/exit 0))