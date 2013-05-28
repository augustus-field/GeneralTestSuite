(ns GeneralTestSuite.basic
   (:require [clj-http.client :as client])
   (:require [clojure.data.json :as json])
   ;(:require [clojure.contrib.duck-streams :only (slurp*)])
   (:use [GeneralTestSuite.config :only [login-routes request-path]])
   (:use [GeneralTestSuite.util])
   )

(defn sgfm-base-request*
  "Peform a request using keys defined in param-path, also return raw response"
  [base-url param-path-value]
  (binding [cookie (get-cookie)]
    (request-get (str base-url param-path-value))))

(defn sgfm-request*
  "Peform a request using keys defined in param-path, return response body"
  [base-url param-path-value]
  (:body (sgfm-base-request* base-url param-path-value) ))

(defn validate-sgfm-request*
  [base-url param-path-value]
  (read-json-str  (str (sgfm-request* base-url param-path-value))))

(defn sgfm-request 
  "Peform a request using the specified key defined in a login-map, return response body and writes logs"
  [key login-map]
  (let [request-path-for-key (key (request-path login-map))
        request-base-path (:base-url login-map)
        output (validate-sgfm-request* 
                request-base-path
                request-path-for-key)] 
	  (wrap-write-log (str 'param-path-key ">>" key ">>" request-path-for-key) 
	             login-map)
	  (wrap-write-log (str output "\n") 
	             login-map)
   output))
(defn sgfm-base-request*
  "Peform a request using keys defined in param-path, also return raw response"
  [base-url param-path-value]
  (request-get (str base-url param-path-value)))

(defn sgfm-request*
  "Peform a request using keys defined in param-path, return response body"
  [base-url param-path-value]
  (:body (sgfm-base-request* base-url param-path-value) ))

(defn validate-sgfm-request*
  [base-url param-path-value]
  (read-json-str  (str (sgfm-request* base-url param-path-value))))

(defn test-all
  "Request all urls defined in param-path. Set to flag-clean to non-nil to append log in output"
  [login-map & flag-clean]
  (if (nil? flag-clean)
    (clean-log (:log-location login-map)))
  (println (str "Testing against:" (:base-url login-map) "\n" "Log location: " (:log-location login-map)))
  (sgfm-request :login login-map)
  (doseq [pkey (keys (request-path login-map))]
    (if-not 
      (= pkey :login)
      (sgfm-request pkey login-map))))

(defn test-all*
  "Request all urls defined in param-path. Set to flag-clean to non-nil to append log in output"
  [login-map test-keys]
  (clean-log (:log-location login-map))
  (println (str "Testing against:" (:base-url login-map) "\n" "Log location: " (:log-location login-map)))
  (sgfm-request :login login-map)
  (let [ks (remove #(some (partial = %) (cons :login-map test-keys))
                   (keys (request-path login-map)))]
    (doseq [pkey ks]
    (sgfm-request pkey login-map))))

; Test-all in parallel using future, login-routes is chosen as a parameter
(defn long-test [route-key] 
  (doseq [x (range 5)] 
    (future (apply test-all [(assoc (route-key login-routes) :log-location (str x "-" (:log-location (route-key login-routes))))]))))

(defn lazy-test []
  "Create a lazy sequence of tests which will be executed only during realization.
   Note that it is not possible to force completion of all threads."
  (let [counter (atom 0N)]
  (repeatedly  
    #(future (apply test-all [(assoc (:route-key login-routes) :log-location (str (swap! counter inc) "-" (:log-location (:route-key login-routes))))])))))


(defn relazy
  "Create a lazy map of multithreaded requests, usage: 
  -start threads: (def result (relazy :login-remote-test 2))
  -harvest results: (map deref result)"
  [route-key limit]
  (doall
    (map-indexed 
      #(future (apply test-all [(assoc (route-key login-routes) :log-location (str %1 %2 "-" (:log-location (route-key login-routes))))]))
      (range limit))))

(defn relazy*
  [route-key limit keys]
  (doall
    (map-indexed 
      #(future (apply test-all*
                      [(assoc (route-key login-routes) :log-location (str %1 %2 "-" (:log-location (route-key login-routes))))]
                      keys))
      (range limit))))

(defn -main
  "Main testing function"
  [ & args]
  (reset! result-code-set #{})
  (time (doall ; Without doall, @result-code-set will return too early
          (let [result (relazy* :login-local-test 1 '(:list-valid) )]
            (map deref result))))
  (println "Result code set: " @result-code-set))
