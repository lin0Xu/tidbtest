package chaos.testcases.service.tools;

import chaos.testcases.service.core.NetworkParam;
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

    /**
     * delay/loss/busy 网络故障注入
     * @param parm
     * @return
     */
    public ResultCode injectNetworkFault(NetworkParam parm){
        if(null==parm || null == parm.getType()) {
            LOGGER.error("Requst param illegal.");
            return ResultCode.FAIL;
        }
        String type=parm.getType();
        String result=null;

        String[] shellArgs = new String[6*2];
        int paramNums=0;
        if(! (parm.getDevice()==null || parm.getDevice().isEmpty()) ){
            shellArgs[paramNums++] = "-i";
            shellArgs[paramNums++]=parm.getDevice();
        }

        if("delay".equals(type)){
            //autotc.sh -i eth0 -d 3000ms sport 7001
            if(! (parm.getDelayTime()==null || parm.getDelayTime().isEmpty()) ){
                shellArgs[paramNums++] = "-d";
                shellArgs[paramNums++]=parm.getDelayTime();
            }
            result = new CaseDispatcher().shellRun(NETWORK_FAULT_INJECT, shellArgs);
        }else if("loss".equals(type)){
            //autotc.sh -i eth0 -l 30% sport 7001
            if(! (parm.getLossPercent()==null || parm.getLossPercent().isEmpty()) ){
                shellArgs[paramNums++] = "-l";
                shellArgs[paramNums++]=parm.getLossPercent();
            }
        }else if("busy".equals(type)){
            //./autotc.sh -b 1kbps src 127.0.0.1 dport 3306
            if(! (parm.getBwCeiling()==null || parm.getBwCeiling().isEmpty()) ){
                shellArgs[paramNums++] = "-b";
                shellArgs[paramNums++]=parm.getBwCeiling();
            }
        }else {
            result = null;
            LOGGER.error("Network falut type need be specified as loss OR delay OR busy.");
        }
        if(! (parm.getSport()==null || parm.getSport().isEmpty()) ){
            shellArgs[paramNums++] = "sport";
            shellArgs[paramNums++]=parm.getSport();
        }
        if(! (parm.getDport()==null || parm.getDport().isEmpty()) ){
            shellArgs[paramNums++] = "dport";
            shellArgs[paramNums++]=parm.getDport();
        }
        if(! (parm.getSrc()==null || parm.getSrc().isEmpty()) ){
            shellArgs[paramNums++] = "src";
            shellArgs[paramNums++]=parm.getSrc();
        }
        if(! (parm.getDst()==null || parm.getDst().isEmpty()) ){
            shellArgs[paramNums++] = "dst";
            shellArgs[paramNums++]=parm.getDst();
        }

        result = new CaseDispatcher().shellRun(NETWORK_FAULT_INJECT, shellArgs);
        return resultValidate(result);
    }

    /**
     * 撤销device网卡上网络故障
     * @param device
     * @return
     */
    public ResultCode revokeNetFault(String device){
        String result=null;
        String[] shellArgs = new String[2];
        shellArgs[0] = "-i";
        shellArgs[1] = device;
        result = new CaseDispatcher().shellRun(NETWORK_FAULT_REVOKE, shellArgs);
        return resultValidate(result);
    }
}

