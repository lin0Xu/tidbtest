package chaos.testcases.service.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static chaos.testcases.service.core.BaseData.DST_DB_CON_STR;
import static chaos.testcases.service.core.BaseData.MAX_SQL_RUN_PARALLEL;

public class CaseExecutor implements Callable<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseExecutor.class);
    ProcessBuilder processBuilder = new ProcessBuilder();

    public static final String CMD_BASE = "bash -c ";
    List<String> cmdAndArgs = new ArrayList<String>();


    String cmd;
    String[] cmdAndArgsArr=null;

    private String shellFileName = "";
    public String getShellFileName() {
        return shellFileName;
    }
    public void setShellFileName(String shellFileName) {
        this.shellFileName = shellFileName;
    }

    /**
     * 多个参数，Run Shell File with input Args; bash xxx.sh args01 args02 args03
     * @param shellFileName: xxx.sh
     * @param shellArgs: args01 args02 args03
     */
    public CaseExecutor(String shellFileName, String[] shellArgs){
        this.setShellFileName(shellFileName);

        cmdAndArgs.add(shellFileName);
        for (String arg: shellArgs){
            if (!(null==arg || arg.isEmpty()))
                cmdAndArgs.add(arg);
        }
        cmdAndArgsArr=new String[cmdAndArgs.size()];
        cmdAndArgs.toArray(cmdAndArgsArr);
        this.setCmd(argsToString(cmdAndArgsArr));
    }

    public String call(){
        Thread.currentThread().setName("_fault_exec_");
        Integer exitCode = -1;

        Future<String> streamRead = null;
        Future<String> errStream = null;
        String result=null;
        Process process=null;
        ExecutorService inputReadExec = Executors.newFixedThreadPool(2);
        ExecutorService errReadExec = Executors.newCachedThreadPool();

        try{
            processBuilder.command(cmdAndArgsArr);

            LOGGER.info(">>BEGIN Run cmd:" + this.cmd);
            process = processBuilder.start();
            exitCode = process.waitFor();

            streamRead = inputReadExec.submit(new StreamRead(process.getInputStream(), "InputStream"));
            errStream = errReadExec.submit(new StreamRead(process.getErrorStream(), "ErrorStream"));

        }catch (IOException e){
            LOGGER.error("## processBuilder.start Encountered IOException");
            e.printStackTrace();
        }catch (InterruptedException e){
            LOGGER.error("## processBuilder.start Encountered InterruptedException");
            e.fillInStackTrace();
        }

        try{
            result=streamRead.get();
            if(errStream.get() !=null && !errStream.get().isEmpty()){
                LOGGER.error("## Thread Read From Thread Unexpected, CMD:" + cmd + ", ERROR MSG:"+errStream.get());
                try{
                    process.getErrorStream().close();
                }catch(IOException e){
                    LOGGER.error("## Close ErrorStream Encountered IOException.");
                    e.printStackTrace();
                }
            }
            LOGGER.info("<<End Run,"+"shell-Return:"+result);
        }catch (ExecutionException e){
            LOGGER.error("## Stream read encountered ExecutionException.");
            e.printStackTrace();
        }catch (InterruptedException e){
            LOGGER.error("## Stream read encountered InterruptedException.");
        }finally {
            if(process!=null)
                try{
                    process.getInputStream().close();
                    process.getErrorStream().close();
                    inputReadExec.shutdown();
                    errReadExec.shutdown();
                    process.destroy();
                }catch (IOException e){
                    LOGGER.error("## Close InputStream Encountered IOException.");
                    e.printStackTrace();
                }
        }

        if(exitCode.equals(0)){
            LOGGER.info("## Shell Run SUCCEED, exit code:" + exitCode);
        }else {
            LOGGER.error("## Shell Run FAILED, exit code:" + exitCode);
            return null;
        }

        return result;
    }


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    /**
     * Shell CMD and Agrs Print.
     * @param cmdAndArgs
     * @return
     */
    private String argsToString(String[] cmdAndArgs){
        StringBuffer sBuf = new StringBuffer();
        for (String arg:cmdAndArgs){
            sBuf.append(arg+" ");
        }
        return sBuf.toString();
    }

    public static void main(String args[]){

        List<String> sqls = new ArrayList<>();
        for(int i=0;i<500;i++){
            String dbName = "tpcds_test_db_"+i;
            String tblName = "et_store_sales" + i;
            String sql = "CREATE DATABASE IF NOT EXISTS " + dbName + ";" +"create table IF NOT EXISTS "+dbName+"."+tblName+ "(ss_sold_date_sk bigint,ss_net_profit decimal(7,2)); show create table "+dbName+"."+tblName+"; drop table "+ dbName+"."+tblName + ";drop database "+dbName+";";

            LOGGER.info("sql_"+i+sql);
            sqls.add(sql);
        }
//        SqlExecutor.sqlBatchRun(10, sqls);

    }

}
