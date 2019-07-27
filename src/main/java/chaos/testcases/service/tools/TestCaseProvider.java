package chaos.testcases.service.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TestCaseProvider {
    private static final Logger logger = LoggerFactory.getLogger(TestCaseProvider.class);
    String shellResult = null;

    public TestCaseProvider(){
        this.shellResult = null;
    }

    private String caseExec(String shellFile, String[] shellArgs){
        ExecutorService executorService = Executors.newCachedThreadPool();
        CaseExecutor shellRun = new CaseExecutor(shellFile, shellArgs);
        Future<String> shellReturn = executorService.submit(shellRun);

        try{
            shellResult = shellReturn.get();
        }catch (InterruptedException e){
            logger.error("Shell Run meet InterruptedException.");
            e.fillInStackTrace();
        }catch (ExecutionException e){
            logger.error("Shell Run meet ExecutionException.");
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
        return shellResult;
    }

    /**
     * fault injection via Shell RUN.
     * @param shellFile
     * @param shellArgs
     * @return
     */
    public String faultInject(String shellFile, String[] shellArgs) {
        String result = caseExec(shellFile, shellArgs);
        return result;
    }
}
