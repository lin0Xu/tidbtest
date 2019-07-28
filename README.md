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
5. 提供单用例执行和批量用例执行的能力：
   每条用例维护一个uuid，通过uuid执行该用例；
   通过用例类型(如：sql_all, sql_ddl_create, sql_ddl_alter, fault_case_all, fault_injection_process,traction_sql…)批量执行该类型所有用例；执行方式可以执行并行度，并行度为1则顺序串行执行。


用例维护和执行：

`应用启动：java -Dserver.port=9091 -jar target/tidb-test-1.0-SNAPSHOT.jar chaos.testcases.service.CaseAgent`

1.故障case:

2.sql case：

2.1. 运行一个 DDL testcase，并保存该case到模板库；
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

暂时包含了：DDL_CREATE、DDL_ALTER两类case;
```

2.2. 运行一个事务case:

```
前置准备：
CREATE DATABASE IF NOT EXISTS transaction_db_test;
create table IF NOT EXISTS transaction_db_test.trans_tbl_test(`id` int(11) NOT NULL AUTO_INCREMENT, `user_id` varchar(128) NOT NULL,`account_val` bigint NOT NULL, PRIMARY KEY (`id`));
alter table transaction_db_test.trans_tbl_test add UNIQUE INDEX (user_id);
INSERT INTO transaction_db_test.trans_tbl_test(user_id, account_val) VALUES ("u_00001", 0);
INSERT INTO transaction_db_test.trans_tbl_test(user_id, account_val) VALUES ("u_00002", 4000);
```

(1) 事务提交case：
```
curl -H "Content-type:application/json" -X POST \
--data '{"saveCase":true,"sqlCase":{"sqlValue":"update transaction_db_test.trans_tbl_test set account_val = (account_val+500) where user_id=\"u_00001\"; update transaction_db_test.trans_tbl_test set account_val = (account_val-500) where user_id=\"u_00002\";","sqlType":"transaction_sql","description":"事务-多次update-commited"},"runOpt":{"loop":1,"parallel":2}}' \
http://localhost:9091/case/sql/submit

| 3508 | eacf84c6-b842-4728-9159-546db786234f | update transaction_db_test.trans_tbl_test set account_val = (account_val+500) where user_id="u_00001"; update transaction_db_test.trans_tbl_test set account_val = (account_val-500) where user_id="u_00002";    | transaction_sql | 事务-多次update-commited                                                 | 1564323052364    |

log:
2019-07-28 22:10:52.528  INFO 36596 --- [ool-15-thread-1] c.t.service.tools.TransactionExecutor    : sql transaction commited.

mysql> select * from trans_tbl_test;
+----+---------+-------------+
| id | user_id | account_val |
+----+---------+-------------+
|  1 | u_00001 |         500 |
|  2 | u_00002 |        3500 |
+----+---------+-------------+
2 rows in set (0.01 sec)

mysql> select * from trans_tbl_test;
+----+---------+-------------+
| id | user_id | account_val |
+----+---------+-------------+
|  1 | u_00001 |        1000 |
|  2 | u_00002 |        3000 |
+----+---------+-------------+
2 rows in set (0.00 sec)

```

(2)事务回滚case:

```
新增一个事务胡回滚case,并保存case；
curl -H "Content-type:application/json" -X POST \
--data '{"saveCase":true,"sqlCase":{"sqlValue":"update transaction_db_test.trans_tbl_test set account_val = (account_val-500) where user_id=\"u_00001\"; update transaction_db_test.trans_tbl_test set account_val = (account_val+500) where user_id=\"u_00002\"; INSERT INTO transaction_db_test.trans_tbl_test(user_id, account_val) VALUES (\"u_00001\", 5000);","sqlType":"transaction_sql","description":"事务-多次update-insert-rollbacked"},"runOpt":{"loop":1,"parallel":2}}' \
http://localhost:9091/case/sql/submit

log：
2019-07-28 22:15:45.323 ERROR 36596 --- [ool-18-thread-1] c.t.service.tools.TransactionExecutor    : sql transcation rollback.

事务-回滚-case：
| 3510 | 40925b7b-5a70-46ff-8a85-253afd9faf14 | update transaction_db_test.trans_tbl_test set account_val = (account_val-500) where user_id="u_00001"; update transaction_db_test.trans_tbl_test set account_val = (account_val+500) where user_id="u_00002"; INSERT INTO transaction_db_test.trans_tbl_test(user_id, account_val) VALUES ("u_00001", 5000);       | transaction_sql | 事务-多次update-insert-rollbacked                                        | 1564323345148    |

mysql> select * from trans_tbl_test;
+----+---------+-------------+
| id | user_id | account_val |
+----+---------+-------------+
|  1 | u_00001 |        1000 |
|  2 | u_00002 |        3000 |
+----+---------+-------------+
2 rows in set (0.00 sec)

mysql> select * from trans_tbl_test;
+----+---------+-------------+
| id | user_id | account_val |
+----+---------+-------------+
|  1 | u_00001 |        1000 |
|  2 | u_00002 |        3000 |
+----+---------+-------------+
2 rows in set (0.00 sec)

```
3. 一个测试场景：
```
测试环境：
机器配置：
    机器规模：单机-macbp
    内存：8GB内存
    磁盘：70GB SSD
    cpu: 2.3 GHz Intel Core i5
部署：使用Docker Compose部署的docker集群：tidb-server x 3, tikv-server x 3, pd x 3, 监控promi x 2, grafa x 1;

```

```
（1）使用脚本调用 以上case执行接口构造稳定压力，目的：让TiDB集群 tps、qps维持在比较稳定的业务状态：
脚本如下：

#! /bin/bash

loop=$1
for i in `seq 1 $loop`;do
curl -X GET "localhost:9091/case/sql/rnd/batchrun?parallel=3&num=3&loop=10"
echo "#i: $i"
sleep 3s
done
echo "#### done"

（2）在恒定业务压力下，注入以下故障，并对应关注：
  （2.1） kill 一个tikv-server进程；
    预期： (a)不影响 DDL-case、事务-case正确执行；
          (b)在kill掉的节点上线后，qps恢复稳定；


   (2.2) 其他场景有：
        分别kill掉3节点pd集群的lead、follower;
        kill掉3节点tidb-server中的1个、2个节点；
        kill掉3节点tikv-server中的1个tikv-server；
        模拟多个tikv-server存储磁盘故障：inode不足、目录写失败、写负载高、读负载高、读写负载高、节点下线、节点频繁上下线；
        模拟tikv-server与pd-leader间网络异常：延迟高、丢包、包乱序；
        模拟tidb-server与pd-leader间网络异常：延迟高、丢包、包乱序；
        模拟tikv-server与tidb-server间网络异常：延迟高、丢包、包乱序；
        模拟pd-cluster内部节点间网络异常对于leader选举，元数据写入的影响；
        模拟tikv-server间网络异常对于数据同步的影响；
        模拟tidb-server节点cpu/内存高负载场景；

        以上故障场景模拟之后，关注的指标有：
        a. 对于业务正确执行的影响；
        b. 对于数据存储正确性的影响；
        c. tpc/qps的影响，以及异常恢复后，tps/qps是否回归正常水位；

```

