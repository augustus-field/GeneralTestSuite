(defproject WebRequest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main GeneralTestSuite.basic
  :repositories {"local" ~(str (.toURI (java.io.File. "/home/cassc/lib/maven-lib")))}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [ring"1.0.0"]
                 [clj-http "0.7.2"]
;                 [org.clojure/java.jdbc "0.2.0"]
                 [org.clojure/java.jdbc "0.3.0-alpha1"] 
[c3p0 "0.9.1.2"]
[ojdbc "14"]
[ttjdbc "6"]
[org.slf4j/slf4j-api "1.5.6"]
[org.slf4j/slf4j-log4j12 "1.5.6"]
[log4j/log4j "1.2.13"]
[ring-basic-authentication "1.0.2"]
[mysql/mysql-connector-java "5.1.12"]
[org.clojure/data.json "0.2.2"]
[clamq/clamq-activemq "0.4"]
[org.apache.activemq/activemq-core "5.5.1"]
[digest "1.3.0"]])
