package chaos.testcases.service.tools;

import chaos.testcases.service.model.SqlCase;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static chaos.testcases.service.core.BaseData.DST_DB_CON_STR;
import static chaos.testcases.service.core.BaseData.MAX_SQL_RUN_PARALLEL;

public class SqlExecutor implements Callable<Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlExecutor.class);

    private String sql;
    private Statement smt;

    public SqlExecutor(String sql, Statement smt){
        this.sql = sql;
        this.smt = smt;
    }

    public Boolean call(){
        Thread.currentThread().setName("_sql_exec_");

        try{
            LOGGER.info("## sql:"+sql);
            return smt.execute(sql);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    public static void signalSqlRun(SqlCase sqlCase, int parallel, int loop){
        Statement stmt = null;
        ResultSet rs = null;

        if(parallel > MAX_SQL_RUN_PARALLEL)
            parallel = MAX_SQL_RUN_PARALLEL;
        ExecutorService executors = Executors.newFixedThreadPool(parallel);

        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(DST_DB_CON_STR);
            stmt = conn.createStatement();
            for (int i=0;i<loop;i++){
                executors.submit(new SqlExecutor(sqlCase.getSqlValue(), stmt));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=executors){
                executors.shutdown();
                executors = null;
            }
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (SQLException sqlEx) { }
//                rs = null;
//            }
//
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (SQLException sqlEx) { }
//                stmt = null;
//            }
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
            stmt = conn.createStatement();

            synchronized (sqls){
                for (String sql:sqls){
                    executors.submit(new SqlExecutor(sql, stmt));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=executors){
                executors.shutdown();
                executors = null;
            }
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (SQLException sqlEx) { }
//                rs = null;
//            }
//
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (SQLException sqlEx) { }
//                stmt = null;
//            }
        }


    }
}

