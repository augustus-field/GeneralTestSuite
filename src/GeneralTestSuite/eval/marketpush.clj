(ns GeneralTestSuite.eval.marketpush
   (:require [clj-http.client :as client])
   (:require [clojure.data.json :as json])
   ;(:require [clojure.contrib.duck-streams :only (slurp*)])
   (:use [GeneralTestSuite.config :only [login-routes request-path]])
   (:use [GeneralTestSuite.util])
   (:require [cheshire.core :refer :all]))


(def thread-count 1)

; Create a vector(maybe a map consisting of eventId->event) from every response and merge this map 
; with previous one.
; Compare this vector periodically with the vector generated from full request

