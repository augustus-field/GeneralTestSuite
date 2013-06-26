(ns GeneralTestSuite.db
   (:import com.mchange.v2.c3p0.ComboPooledDataSource)
   (:use [ring.adapter.jetty :only (run-jetty)])
   (:use [ring.middleware.params :only (wrap-params)])
   (:require [clojure.java.jdbc :as j]
         [clojure.java.jdbc.sql :as sql]))

;; Oracle db
(def ora-db {:classname "oracle.jdbc.driver.OracleDriver"
             :subprotocol "oracle"
             :subname "thin:@172.17.110.19:1521:SGFM" 
             :user "tflatopt"
             :password "tflatopt"})

;timesten connection
(def tt-db {:classname "com.timesten.jdbc.TimesTenClientDriver"
             :subprotocol "timesten"
             :subname "client:dsn=DEV_TT" 
             :user "tflatown"
             :password "tflatown"})

(def tt-db-remote {:classname "com.timesten.jdbc.TimesTenClientDriver"
             :subprotocol "timesten"
             :subname "client:dsn=Test_TT" 
             :user "tflatown"
             :password "tflatown"})

;mysql
(def mysql-db {:classname "com.mysql.jdbc.Driver"
             :subprotocol "mysql"
             :subname "//localhost:3306/test" 
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
               (.setInitialPoolSize 2)
               (.setMaxPoolSize 5)
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 5 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(def pool-ora
  "Oracle connection pool"
  (delay (pool ora-db)))

(def pool-tt
  "Timesten connection pool"
  (delay (pool tt-db)))

(def pool-mysql
  (delay (pool mysql-db)))

(def pool-tt-remote
  (delay (pool tt-db-remote)))

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
      result
      )))
;; TODO Should memoize these results to avoid quering db everytime

;; TODO Code duplication
(defn query-for-selections
  "Query all tradable selections from a database connection"
  [db-pool]
  (let [connection (conn db-pool) 
        sql-str (str "select t.tradingitemid tid from t_tradingitem t" 
                     " inner join t_gaminginfo g on t.gamingid=g.gamingid and g.status=7")]
  (j/with-connection connection
    (j/with-query-results result [sql-str]
      ; convert result to map
      ; Use doall to prevent laziness, since db will close after evaluating this function.
      (doall (map :tid result))
      ;result
      ))))

(defn query-for-order-ids
  [db-pool login-name]
  (let [connection (conn db-pool) 
        sql-str (str "select t.intentionid oid from t_intention t inner join t_userinfo u on t.traderid=u.userid and  u.login_name='"
                     login-name
                     "' and intentionstatus=1 where rownum<50")]
  (j/with-connection connection
    (j/with-query-results result [sql-str]
      (doall (map :oid result))
      ))))

(defn query-for-match-ids
  [db-pool]
  (let [connection (conn db-pool) 
        sql-str "select matchid from t_matchinfo where status in (3,4) and rownum<50"]
  (j/with-connection connection
    (j/with-query-results result [sql-str]
      (doall (map :matchid result))
      ))))


(defn query-mysql-test
  []
  (let [conn (conn pool-mysql)
        sql-str (str "select * from t_intention")]
    (j/with-connection conn
      (j/with-query-results result [sql-str]
        (doall (map :id result))))))

(defn get-usable-accounts 
  "Not implemented"
  [] nil)
(defn get-selections 
  "Get tradable selections, NOT WORKING"
  []
  (let [result (doall (query  
                 "select t.tradingitemid tid from t_tradingitem t inner join t_gaminginfo g on t.gamingid=g.gamingid and g.status=7"))]
  (doseq [r result]
    ;; TODO Add ids to a set and return
    ;; Use only immutable intermidiate data 
    (println  r)
    ;nil
    )))
