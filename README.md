tidbtest

测试框架-初期目标：

1. 可扩展性：
   - 用例补充的可扩展性：动态可扩展用例集；
   
2. 具备故障注入能力：
   - 故障类型：
   - 故障触发和撤销：
   - 故障时长：
3. 



实现思路：

1. 用例存储库：
   需要有地方统一存储维护用例集合，使用TiDB存储；
2. 用例触发方式：
   通过提供api调用 的方式，向宿主机注入故障 或 从宿主机执行用例；每次故障注入调用成功都响应一个uuid；
3. 故障撤销方式：
   api调用指定uuid撤销关联注入的故障；故障持续时长由调用方通过触发故障和撤销故障控制；
4. 用例新增和立即执行能力：
   提供API方式，通过参数可控制，用例一次执行的用例是否保存到用例库；
5. 提供但用例执行和批量用例执行的能力：
   每条用例维护一个uuid，通过uuid执行该用例；
   通过用例类型(如：sql_all, sql_ddl_create, sql_ddl_alter, fault_case_all, fault_injection_process,…)批量执行该类型所有用例；执行方式可以执行并行度，并行度为1则顺序串行执行。
6. 



简单使用：
1. 运行一个新sql testcase，并保存该case到模板库；
```
curl -H "Content-type:application/json" -X POST \
--data '{"saveCase":true,
	"sqlCase":{
		"sqlValue":"CREATE DATABASE IF NOT EXISTS tpcds_test_db; use tpcds_test_db;create table IF NOT EXISTS et_store_sales(ss_sold_date_sk bigint,ss_net_profit decimal(7,2)); show create table et_store_sales; drop table et_store_sales;drop database tpcds_test_db;",
    	"sqlType":"DDL_CREATE",
    	"description":"tpcds_sql-创建DB-创建表-删除表-删除db"
	}
}' \
http://localhost:9091/case/sql/case/run

+------+--------------------------------------+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+------------+-------------------------------------------------+------------------+
| id   | uuid                                 | sql_val                                                                                                                                                                                                                                               | sql_type   | description                                     | create_timestamp |
+------+--------------------------------------+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+------------+-------------------------------------------------+------------------+
| 1008 | 9f732452-3bb8-4109-8063-5807835e3224 | CREATE DATABASE IF NOT EXISTS tpcds_test_db; use tpcds_test_db;create table IF NOT EXISTS et_store_sales(ss_sold_date_sk bigint,ss_net_profit decimal(7,2)); show create table et_store_sales; drop table et_store_sales;drop database tpcds_test_db; | DDL_CREATE | tpcds_sql-创建DB-创建表-删除表-删除db           | 1564236215090    |
| 2506 | 4809bda2-874f-48b4-99c5-284752c2b045 | CREATE DATABASE IF NOT EXISTS tpcds_test_db_0;use tpcds_test_db_0;create table IF NOT EXISTS et_store_sales0( ss_sold_date_sk bigint, ss_sold_time_sk bigint, ss_item_sk bigint, ss_customer_sk bigint, ss_cdemo_sk bigint, ss_hdemo_sk bigint, ss_addr_sk bigint, ss_store_sk bigint, ss_promo_sk bigint, ss_ticket_number bigint, ss_quantity int, ss_wholesale_cost decimal(7,2), ss_list_price decimal(7,2), ss_sales_price decimal(7,2), ss_ext_discount_amt decimal(7,2), ss_ext_sales_price decimal(7,2), ss_ext_wholesale_cost decimal(7,2), ss_ext_list_price decimal(7,2), ss_ext_tax decimal(7,2), ss_coupon_amt decimal(7,2), ss_net_paid decimal(7,2), ss_net_paid_inc_tax decimal(7,2), ss_net_profit decimal(7,2)); ALTER TABLE et_store_sales0 ADD column_test bigint; ALTER TABLE et_store_sales0 DROP column_test; drop table et_store_sales0;drop database tpcds_test_db_0; | DDL_ALTER  | tpcds_sql-创建DB-创建表-修改表-删除表-删除db              | 1564280256913    |
| 2507 | cd791074-6de6-4f8a-b921-5449eb282ded | CREATE DATABASE IF NOT EXISTS tpcds_test_db_0;use tpcds_test_db_0;create table IF NOT EXISTS et_store_sales0( ss_sold_date_sk bigint, ss_sold_time_sk bigint, ss_item_sk bigint, ss_customer_sk bigint, ss_cdemo_sk bigint, ss_hdemo_sk bigint, ss_addr_sk bigint, ss_store_sk bigint, ss_promo_sk bigint, ss_ticket_number bigint, ss_quantity int, ss_wholesale_cost decimal(7,2), ss_list_price decimal(7,2), ss_sales_price decimal(7,2), ss_ext_discount_amt decimal(7,2), ss_ext_sales_price decimal(7,2), ss_ext_wholesale_cost decimal(7,2), ss_ext_list_price decimal(7,2), ss_ext_tax decimal(7,2), ss_coupon_amt decimal(7,2), ss_net_paid decimal(7,2), ss_net_paid_inc_tax decimal(7,2), ss_net_profit decimal(7,2)); ALTER TABLE et_store_sales0 CHANGE COLUMN ss_cdemo_sk ss_cdemo_sk_new_test bigint; drop table et_store_sales0;drop database tpcds_test_db_0;                | DDL_ALTER  | tpcds_sql-创建DB-创建表-修改表(change column)-删除表-删除db              | 1564281029228    |
```

