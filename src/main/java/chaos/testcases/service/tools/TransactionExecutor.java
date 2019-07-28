package chaos.testcases.service.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.Callable;

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
}
