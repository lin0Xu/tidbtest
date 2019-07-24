package chaos.testcases.service.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

/**
 * StreamRead 提供了多线程读取线程stdout,stdin,stderr缓冲区内容的能力；
 */
public class StreamRead implements Callable<String> {
    private static final Logger logger = LoggerFactory.getLogger(StreamRead.class);
    private final BufferedReader bufReader;
    private final String type;
    private StringBuffer sb = new StringBuffer();

    public StreamRead(InputStream ins, String type){
        bufReader = new BufferedReader(new InputStreamReader(ins));
        this.type = type;
    }

    public String call(){
        while (!Thread.currentThread().isInterrupted()){
            try{
                String line = bufReader.readLine();
                if(line==null)
                    break;
                sb.append(line);
            }catch(IOException e){
                logger.error("## Read thread stream Exception.");
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
