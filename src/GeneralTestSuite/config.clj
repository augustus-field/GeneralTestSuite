(ns GeneralTestSuite.config
  (:use [GeneralTestSuite.util :only [get-time-one-day-ago get-time-one-week-ago]])
  (:use [GeneralTestSuite.db]))



(def login-routes
  "Map of login-maps, including request base address, login info and some configurations."
  ;; local 8084
  {:login-local-test {:name "fbbbcc", 
	   :password "111111",
	   :settled (str (rand-int 2)),
	   :log-location "local-84-output.log"
     :base-url "http://172.17.109.92:8084/SgfmApi/"
     :use-local-db true
     :suppress-console true}
   :login-local-ssl {:name "fbbbcc", 
	   :password "111111",
	   :settled (str (rand-int 2)),
	   :log-location "local-84-output.log"
     :base-url "https://172.17.109.92:8443/SgfmApi/"
     :use-local-db true
     :suppress-console true}
   :login-remote {:name "fbfbfb", 
	   :password "111111",
	   :settled (str (rand-int 2)),
	   :log-location "remote-output.log"
     :base-url "http://172.17.108.64:8080/SgfmApi/"
     :use-local-db nil
     :suppress-console true}
   :login-remote-test {:name "fbccbb", 
	   :password "123456",
	   :settled (str (rand-int 2)),
	   :log-location "remote-output.log"
     :base-url "http://172.17.108.64:8088/SgfmApi/"
     :use-local-db true}
   :login-local-172 {:name "fbfbfb", 
	   :password "111111",
	   :settled (str (rand-int 2)),
	   :log-location "local-172.log"
     :base-url "http://localhost:8084/SgfmApi/"
     :use-local-db nil
     :suppress-console true}
   ;; local 8088
   :login-local-stable {:name "fbbbcc", 
	   :password "111111",
	   :settled (str (rand-int 2)),
	   :log-location "local-80-output.log"
     :base-url "http://172.17.109.92:8088/SgfmApi/"
     :use-local-db true
     :suppress-console true}})

(defn get-rand-tradable-id [use-local-db]
  "Get a random tradable id from local or remote timesten server.
   NOT EFFICIENT, for small dataset only!"
  (.intValue (rand-nth (if use-local-db 
    (query-for-selections pool-tt)
    (query-for-selections pool-tt-remote)))))

(defn request-path 
  "Request path construction using configurations passed in login-map "
  [login-map]
  {:login (format "login.sv?param={'un':'%s','pwd':'%s'}" (login-map :name) (login-map :password)), 
   ;; orderManage.sv
   :list-valid (format "orderManage.sv?act=getValidOrderList&param={st_date:'%s', settled:'%s'}" (get-time-one-week-ago) (login-map :settled)),
   :list-invalid (format "orderManage.sv?act=getInvalidOrderList&param={order_stas:[7,8],st_date:'%s', settled:'%s'}" (get-time-one-day-ago) (login-map :settled)),
   :list-all-pending (format "orderManage.sv?act=getOrderList&param={st_date:'%s',order_sta:1}" (get-time-one-week-ago)),
   :list-all-pending-no-args (str "orderManage.sv?act=getOrderList&param={}")
   :list-all-settled (format "orderManage.sv?act=getOrderList&param={st_date:'%s',settled:'%s'}" (get-time-one-week-ago) (login-map :settled)),
   :order-add (format "orderManage.sv?act=addOrder&param={'orders':[{'type':3,'direction':2,'price':1.32,'stake':100, 'sel_id':%d}]}" 
                      (get-rand-tradable-id (login-map :use-local-db))),
   :order-update "orderManage.sv?act=updOrder&param={order_id:1000000002944, price:0.86, stake:12000}", ;todo get rand-order-id
   :order-add-with-expire (format "orderManage.sv?act=addOrder&param={'orders':[{'type':3,'direction':2,'price':1.98,'stake':100, 'sel_id':%d,'expire':12}]}"
                                  (get-rand-tradable-id (login-map :use-local-db))),
   ;:order-undo "orderManage.sv?act=undoOrder",
   :order-add-multiple (format (str "orderManage.sv?act=addOrder&param=" 
                            "{'orders':[{'type':3,'direction':1,'price':1.23,'stake':100, 'sel_id':%d,'expire':12},"
                            "{'type':1,'direction':1,'price':1.24,'stake':10000, 'sel_id':%d,'time_out':59},"
                            "{'type':3,'direction':1,'price':1.68,'stake':8000, 'sel_id':%d,'expire':10}]}")
                               (get-rand-tradable-id (login-map :use-local-db)) 
                               (get-rand-tradable-id (login-map :use-local-db)) 
                               (get-rand-tradable-id (login-map :use-local-db))), ; todo: ugly
   ;:order-push "orderManage.sv?act= pushOrder"
   ;; marketINfo.sv
   :list-markets "marketInfo.sv?",
   :list-markets-selection (format "marketInfo.sv?act=getSelections&param={sel_ids:[%d,%d,%d]}"
                                   (get-rand-tradable-id (login-map :use-local-db)) 
                               (get-rand-tradable-id (login-map :use-local-db)) 
                               (get-rand-tradable-id (login-map :use-local-db))), ; todo: ugly
   :list-leagues "marketInfo.sv?act=getLeague",
   :list-markets-cond "marketInfo.sv?param={language:'EN','lg_ids':[],'event_ids':[],'ver_num':52}", ; todo get valid vernum,
   :query-account (format "account.sv?param={'un':'%s'}" (login-map :name))
   :get-hb "getHeart.sv?time_out=10"
   :cancel-hb "getHeart.sv"
   })
