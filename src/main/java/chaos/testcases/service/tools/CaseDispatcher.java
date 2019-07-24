package chaos.testcases.service.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CaseDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseDispatcher.class);
    String shellResult = null;

    public String shellRun(String shellFile, String[] shellArgs){
        ExecutorService executorService = Executors.newCachedThreadPool();
        CaseExecutor executor = new CaseExecutor(shellFile, shellArgs);
        Future<String> shellReturn = executorService.submit(executor);

        try{
            shellResult = shellReturn.get();
        }catch (InterruptedException e){
            LOGGER.error("Shell Run meet InterruptedException.");
            e.fillInStackTrace();
        }catch (ExecutionException e){
            LOGGER.error("Shell Run meet ExecutionException.");
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
        return shellResult;
    }
}
