(ns GeneralTestSuite.db
   (:import com.mchange.v2.c3p0.ComboPooledDataSource)
   (:use [ring.adapter.jetty :only (run-jetty)])
   (:use [ring.middleware.params :only (wrap-params)])
   (:require [clojure.java.jdbc :as j]
         [clojure.java.jdbc.sql :as sql]))

;; Oracle db
(def ora-db {:classname "oracle.jdbc.driver.OracleDriver"
             :subprotocol "oracle"
             :subname "thin:@172.17.108.42:1521:SGFM" 
             :user "tflatopt"
             :password "tflatopt"})

;timesten connection
(def tt-db {:classname "com.timesten.jdbc.TimesTenClientDriver"
             :subprotocol "timesten"
             :subname "client:dsn=TT" 
             :user "tflatown"
             :password "tflatown"})

;mysql
(def mysql-db {:classname "com.mysql.jdbc.Driver"
             :subprotocol "mysql"
             :subname "//localhost:3306/mysql" 
             :user "root"
             :password "cern"})

; c3p0 datasource
(defn pool
  "Create a connection pool using db profile provided"
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               (.setInitialPoolSize 5)
               (.setMaxPoolSize 5)
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pool-ora
  "Oracle connection pool"
  (delay (pool ora-db)))

(def pool-tt
  "Timesten connection pool"
  (delay (pool tt-db)))

(defn conn
  "A connection from pool"
  [pool-db]
  @pool-db)

;(j/query ora-db
;  (sql/select * "user_tables")
;  :row-fn :cost)

(defn print-result  [result]
  (doseq [rec result]
    (println rec)))


(defn query
  "Query a sql string from a database connection, 
  either direct or indirect from a db pool"
  [connection sql-str]
  (j/with-connection connection
    (j/with-query-results result [sql-str]
      ; convert result to map
      ;(doseq [rs result]  (println rs))
      (cons [] result)
      )))

(defn get-usable-accounts 
  "Not implemented"
  [] nil)
(defn get-selections 
  "Get tradable selections"
  []
  (let [result (query (conn pool-ora) 
                 "select t.tradingitemid tid from t_tradingitem t inner join t_gaminginfo g on t.gamingid=g.gamingid and g.status in (1,2,3,4,5,6,7,8)")]
  (doseq [r result]
    ;; TODO Add ids to a set and return
    ;; Use only immutable intermidiate data 
    (println  r)
    nil
    )
  ))
