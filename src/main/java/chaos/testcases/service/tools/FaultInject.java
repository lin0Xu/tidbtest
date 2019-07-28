package chaos.testcases.service.tools;

import chaos.testcases.service.core.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static chaos.testcases.service.core.BaseData.*;

public class FaultInject {
    private static final Logger LOGGER = LoggerFactory.getLogger(FaultInject.class);

    private static String diskFillTmpFile = "disk_tmp_file_";

    /**
     * 磁盘 path 目录 下fill
     * @param path
     * @param blockSize
     * @param count
     * @return 文件目录填充产生的临时文件绝对路径，用于故障撤销删除；
     */
    public String fillDisk(String path, String blockSize, String count){
        String tmpFile;
        if(path.substring(path.length()-1, path.length()).equals("/"))
            tmpFile = path+diskFillTmpFile+System.currentTimeMillis();
        else
            tmpFile = path+"/"+diskFillTmpFile+System.currentTimeMillis();

        String[] shellArgs = new String[3];
        shellArgs[0] = tmpFile;
        shellArgs[1] = blockSize;
        shellArgs[2] = count;

        String fileCreated = new CaseDispatcher().shellRun(DISK_CAPACITY_FILL, shellArgs);
        if(null == fileCreated)
            return null;
        else
            return fileCreated;
    }


    public String revokeFillDisk(String fileName){
        String[] shellArgs = new String[1];
        shellArgs[0] = fileName;
        String fileDeled = new CaseDispatcher().shellRun(DISK_CAPACITY_FILL_REVOKE, shellArgs);

        if(null == fileDeled)
            return null;
        else
            return fileDeled;
    }

    /**
     *  注入 disk IO busy 异常
     * @param deviceDir
     * @param type
     * @return
     */
    public ResultCode injectDiskIOBusy(String deviceDir, String type){
        String tmpFile;
        if(deviceDir.substring(deviceDir.length()-1, deviceDir.length()).equals("/"))
            tmpFile = deviceDir+diskFillTmpFile+System.currentTimeMillis();
        else
            tmpFile = deviceDir+"/"+diskFillTmpFile+System.currentTimeMillis();

        String[] shellArgs = new String[2];
        shellArgs[0] = tmpFile;
        shellArgs[1] = type;
        String result = new CaseDispatcher().shellRun(DISK_IO_BUSY_INJECT, shellArgs);
        return resultValidate(result);
    }

    /**
     * 撤销 deviceDir 上的type类型 fio故障
     * @param deviceDir
     * @return
     */
    public ResultCode revokeDiskIOBusy(String deviceDir, String type){
        String[] shellArgs = new String[3];
        shellArgs[0] = deviceDir;
        shellArgs[1] = type;
        shellArgs[2] = DISK_IO_BUSY_INJECT;
        String result = new CaseDispatcher().shellRun(DISK_IO_BUSY_REVOKE, shellArgs);
        return resultValidate(result);
    }

    private ResultCode resultValidate(String result){
        if(null==result)
            return ResultCode.FAIL;
        else
            return ResultCode.SUCCESS;
    }
}
