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
