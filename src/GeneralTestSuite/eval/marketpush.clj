(ns GeneralTestSuite.eval.marketpush
   (:require [clj-http.client :as client])
;   (:require [clojure.data.json :as json])
   ;(:require [clojure.contrib.duck-streams :only (slurp*)])
   (:use [GeneralTestSuite.config :only [login-routes request-path]])
   (:use [GeneralTestSuite.basic :only [sgfm-request sgfm-request*]])
   (:use [GeneralTestSuite.util])
   (:use [clojure.pprint])
   (:require [cheshire.core]))


(def thread-count 1)

; Create a vector(maybe a map consisting of eventId->event) from every response and merge this map 
; with previous one.
; Compare this vector periodically with the vector generated from full request


(defn get-full-market-list
  "Get market list raw data"
  [login-profile-key]
;  (sgfm-request :login (login-profile-key login-routes))
  (let [full-market-list (sgfm-request :list-markets (login-profile-key login-routes))]
    full-market-list))

(defn get-market-list-by-version
  "Get market list incremental data from version-num and using profile for login-profile-key in login-routes. 
  Must login first."
  [login-profile-key version-num]
  (let [login-map (login-profile-key login-routes)
        request-path-market-list-by-ver (format "marketInfo.sv?param={'ver_num':%s}"  version-num) 
        request-path-base (:base-url login-map)
        output (sgfm-request* 
                request-path-base
                request-path-market-list-by-ver)] 
	  (wrap-write-log (str output "\n") 
                    login-map)
    output))

(defn get-ver-num 
  "Get version number from raw market info response"
  [raw-market-list]
  (let [jmap (cheshire.core/parse-string raw-market-list)]
    (get jmap "ver_num")))

(def inc-market-list (atom nil))

;; merge, how?
;; (defn merge-list [old-list new-list]
;;   (for ))

(defn update-market-list-by-inc 
  "TODO Update/merge udpated market info"
  [raw-market-list]
  (let [market-list (get raw-market-list "events")]
    (pprint market-list)))

(defn recur-request 
  "Recursively request for incremental market info"
  [login-profile-key vernum]
  (let [full-market-list (get-full-market-list login-profile-key)
        new-raw-market-list (get-market-list-by-version login-profile-key vernum)
        new-vernum (get-ver-num new-raw-market-list)]
    (update-market-list-by-inc new-raw-market-list)
    (recur login-profile-key new-vernum)))


(defn start-market-list-validation [login-profile-key]
  (sgfm-request :login (login-profile-key login-routes))
  (recur-request login-profile-key 0))




