package chaos.testcases.service.tools;

import chaos.testcases.service.model.SqlCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static chaos.testcases.service.core.BaseData.DST_DB_CON_STR;
import static chaos.testcases.service.core.BaseData.MAX_SQL_RUN_PARALLEL;

public class SqlExecutor2 implements Callable<Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlExecutor2.class);

    // 一个sqlCase大多情况包含多条sql语句；
    private String[] sqlCases;
    private Connection con;

    public SqlExecutor2(String[] sqlCases, Connection con){
        this.sqlCases = sqlCases;
        this.con = con;
    }

    public Boolean call(){
//        Thread.currentThread().setName("_sql_exec_");
        try{
            synchronized (sqlCases){
                for(String sqlCase: sqlCases){
                    String[] sqlArr = sqlCase.split(";");
                    Statement stmt = con.createStatement();
                    LOGGER.info("### Begin sql RUN.");
                    for (int i=0;i<sqlArr.length;i++){
                        sqlArr[i] = sqlArr[i].trim();
                        if(sqlArr[i].length()>2) {
                            LOGGER.info("#i:"+i+",sql:"+sqlArr[i]);
                            stmt.execute(sqlArr[i]);
                        }
                    }
                    LOGGER.info("++sql:"+sqlCase);
                    LOGGER.info("### END sql RUN.");
                }
            }

        }catch(SQLException e){
            LOGGER.error("sql Run Unexpected.");
            e.printStackTrace();
        }

        return true;
    }

    public static void signalSqlRun(SqlCase sqlCase, int parallel, int loop){
        Statement stmt = null;
        ResultSet rs = null;

        if(parallel > MAX_SQL_RUN_PARALLEL)
            parallel = MAX_SQL_RUN_PARALLEL;
        ExecutorService executors = Executors.newFixedThreadPool(parallel);
        String[] sql = new String[1];
        sql[0] = sqlCase.getSqlValue();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(DST_DB_CON_STR);
            for (int i=0;i<loop;i++){
                executors.submit(new SqlExecutor2(sql, conn));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null!=executors && executors.isTerminated()){
                LOGGER.info("+++++++++++++++++++++++");
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

    public static void sqlBatchRun(List<SqlCase> sqlCases, int parallel){
        if(null==sqlCases || sqlCases.size()==0){

        }
        else {
            List<String> sqls = new ArrayList<>();
            for(SqlCase sqlCase:sqlCases){
                if(sqlCase!=null && !sqlCase.getSqlValue().isEmpty()){
                    sqls.add(sqlCase.getSqlValue());
                }else{
                    continue;
                }
            }
            sqlBatchRun(parallel, sqls);
        }
    }

    public static void sqlBatchRun(int parallel, List<String> sqls){
        Statement stmt = null;
        ResultSet rs = null;

        if(parallel > MAX_SQL_RUN_PARALLEL)
            parallel = MAX_SQL_RUN_PARALLEL;
        ExecutorService executors = Executors.newFixedThreadPool(parallel);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(DST_DB_CON_STR);

            String[] sqlArr = new String[sqls.size()];
            sqls.toArray(sqlArr);
            executors.submit(new SqlExecutor2(sqlArr, conn));

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=executors){
                executors.shutdown();
                executors = null;
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { }
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { }
                stmt = null;
            }
        }
    }


    public static void main(String args[]){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(DST_DB_CON_STR);
            String sqlCase = "CREATE DATABASE IF NOT EXISTS tpcds_test_db_199;use tpcds_test_db_199;create table IF NOT EXISTS et_store_sales199(ss_sold_date_sk bigint,ss_net_profit decimal(7,2)); show create table et_store_sales199; drop table et_store_sales199;drop database tpcds_test_db_199;\n";

            try{
                String[] sqlArr = sqlCase.split(";");
                Statement stmt = con.createStatement();
                LOGGER.info("### Begin sql RUN.");
                for (int i=0;i<sqlArr.length;i++){
                    sqlArr[i] = sqlArr[i].trim();
                    if(sqlArr[i].length()>2) {
                        LOGGER.info("#i:"+i+",sql:"+sqlArr[i]);
                        stmt.execute(sqlArr[i]);
                    }
                }
                LOGGER.info("### END sql RUN.");
            }catch(SQLException e){
                LOGGER.error("sql Run Unexpected.");
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
        }
    }
}

