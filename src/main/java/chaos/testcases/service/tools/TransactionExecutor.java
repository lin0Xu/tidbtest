package chaos.testcases.service.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static chaos.testcases.service.core.BaseData.DST_DB_CON_STR;

public class TransactionExecutor implements Callable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionExecutor.class);

    private String sqlCase;
    private Connection con;

    public TransactionExecutor(String sqlCase, Connection con){
        this.sqlCase = sqlCase;
        this.con = con;
    }

    public String call(){
//        Thread.currentThread().setName("_trans_exec_");
        try{
            con.setAutoCommit(false);
            String[] sqlArr = sqlCase.split(";");
            Statement stmt = con.createStatement();

            System.out.println("#0:"+sqlArr[0]);
            for (int i=0;i<sqlArr.length;i++){
                sqlArr[i] = sqlArr[i].trim();
                System.out.println("#i:"+i+",sql:"+sqlArr[i]);
                if(sqlArr[i].length()>2) {
                    stmt.execute(sqlArr[i]);
                }
            }
            con.commit();
            LOGGER.info("sql transaction commited.");
        }catch(SQLException e){
            try{
                con.rollback();
                e.printStackTrace();
                LOGGER.error("sql transcation rollback.");
                return "FIALED";
            }catch (SQLException rollbackE){
                LOGGER.error("rollback unexpected.");
                rollbackE.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try{
                con.setAutoCommit(true);
            }catch (SQLException e){
                LOGGER.error("connection reset autoCommit true unexpected.");
                e.printStackTrace();
            }
        }
        return "SUCCEED";
    }


    public static void main(String args[]){

        String sql = "CREATE DATABASE IF NOT EXISTS transaction_db_test; create table IF NOT EXISTS transaction_db_test.trans_tbl_test(`id` int(11) NOT NULL AUTO_INCREMENT, `user_id` varchar(128) NOT NULL,`account_val` bigint NOT NULL, PRIMARY KEY (`id`));alter table transaction_db_test.trans_tbl_test add UNIQUE INDEX (user_id); INSERT INTO transaction_db_test.trans_tbl_test(user_id, account_val) VALUES ('u_00001', 1000); INSERT INTO transaction_db_test.trans_tbl_test(user_id, account_val) VALUES ('u_00002', 3000); update transaction_db_test.trans_tbl_test set account_val = (account_val-500) where user_id='u_00001'; update transaction_db_test.trans_tbl_test set account_val = (account_val+500) where user_id='u_00002'; drop table transaction_db_test.trans_tbl_test;drop database transaction_db_test;";
        Statement stmt = null;
        ResultSet rs = null;

        ExecutorService executors = Executors.newFixedThreadPool(1);

        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(DST_DB_CON_STR);
            executors.submit(new TransactionExecutor(sql, conn));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null!=executors && executors.isTerminated()){
                executors.shutdown();
                executors = null;
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) { }
                    rs = null;
                }
            }
        }
    }
}
