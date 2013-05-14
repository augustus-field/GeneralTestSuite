(ns GeneralTestSuite.config
  (:use [GeneralTestSuite.util :only [get-time-one-day-ago get-time-one-week-ago]]))


(def login-routes
  "Map of login-maps, including request base address, login info and some configurations."
  ;; local 8084
  {:login-local-test {:name "fbccbb", 
	   :password "123456",
	   :settled "0",
	   :log-location "local-84-output.log"
     :base-url "http://172.17.109.92:8084/SgfmApi/"
     :suppress-console true}
   :login-remote {:name "fbwwjj", 
	   :password "111111",
	   :settled "1",
	   :log-location "remote-output.log"
     :base-url "http://172.17.108.64:8080/SgfmApi/"
     :suppress-console true}
   :login-remote-test {:name "fbccbb", 
	   :password "123456",
	   :settled "1",
	   :log-location "remote-output.log"
     :base-url "http://172.17.108.64:8088/SgfmApi/"
     :suppress-console true}
   ;; local 8088
   :login-local-stable {:name "fbbbcc", 
	   :password "111111",
	   :settled "0",
	   :log-location "local-80-output.log"
     :base-url "http://172.17.109.92:8088/SgfmApi/"
     :suppress-console true}})


(defn request-path 
  "Request path construction using configurations passed in login-map "
  [login-map]
  {:login (format "login.sv?param={'un':'%s','pwd':'%s'}" (login-map :name) (login-map :password)), 
   ;; orderManage.sv
   :list-valid (format "orderManage.sv?act=getValidOrderList&param={st_date:'%s', settled:'%s'}" (get-time-one-week-ago) (login-map :settled)),
   :list-invalid (format "orderManage.sv?act=getInvalidOrderList&param={st_date:'%s', settled:'%s'}" (get-time-one-day-ago) (login-map :settled)),
   :list-all-pending (format "orderManage.sv?act=getOrderList&param={st_date:'%s',order_sta:1}" (get-time-one-week-ago)),
   :list-all-settled (format "orderManage.sv?act=getOrderList&param={st_date:'%s',settled:'%s'}" (get-time-one-week-ago) (login-map :settled)),
   :order-add "orderManage.sv?act=addOrder&param={'orders':[{'type':3,'direction':2,'price':1.32,'stake':100, 'sel_id':5083}]}",
   :order-update "orderManage.sv?act=updOrder&param={order_id:1000000002944, price:0.86, stake:12000}",
   :order-add-with-expire "orderManage.sv?act=addOrder&param={'orders':[{'type':3,'direction':2,'price':1.98,'stake':100, 'sel_id':5086,'expire':3}]}",
   :order-undo "orderManage.sv?act=undoOrder",
   :order-add-multiple (str "orderManage.sv?act=addOrder&param=" 
                            "{'orders':[{'type':3,'direction':1,'price':0.98,'stake':100, 'sel_id':4878,'expire':3},"
                            "{'type':1,'direction':1,'price':0.96,'stake':10000, 'sel_id':4894,'time_out':59},"
                            "{'type':3,'direction':1,'price':0.97,'stake':8000, 'sel_id':4902,'expire':3}]}"),
   :order-push "orderManage.sv?act= pushOrder"
   ;; marketINfo.sv
   :list-markets "marketInfo.sv?",
   :list-markets-selection "marketInfo.sv?act=getSelections&param={sel_ids:[4682,4684,4580]}",
   :list-leagues "marketInfo.sv?act=getLeague",
   :list-markets-cond "marketInfo.sv?param={language:'EN','lg_ids':[],'event_ids':[],'ver_num':52}&d=1366363770897",
   :query-account "account.sv?param={'un':'fbbbcc'}"
   :get-hb "getHeart.sv?time_out=10"
   :cancel-hb "getHeart.sv"
   })
