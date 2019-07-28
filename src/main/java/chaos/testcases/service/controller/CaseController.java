package chaos.testcases.service.controller;

import chaos.testcases.service.core.Result;
import chaos.testcases.service.core.ResultCode;
import chaos.testcases.service.model.SqlCase;
import chaos.testcases.service.model.SqlCaseTemplate;
import chaos.testcases.service.repository.SqlCaseRepository;
import chaos.testcases.service.tools.CaseDispatcher;
import chaos.testcases.service.tools.FaultInject;
import chaos.testcases.service.tools.SqlExecutor;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static chaos.testcases.service.core.BaseData.*;

@RestController
@RequestMapping("/case")
public class CaseController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SqlCaseRepository sqlCaseRepository;

    @GetMapping("/faultinjection/process/kill")
    public String injectPsFaultViaFlag(@RequestParam("flag") String serviceFlag, @RequestParam("signal") Integer signal){
        String[] shellArgs = new String[2];
        shellArgs[0]=serviceFlag;
        shellArgs[1]=String.valueOf(signal);
        return new CaseDispatcher().shellRun(PROCESS_KILL,shellArgs);
    }

    @GetMapping("/faultinjection/process/renice")
    public String injectPsFaultViaPid(@RequestParam("flag") String serviceFlag, @RequestParam("signal") Integer signal){
        String[] shellArgs = new String[2];
        shellArgs[0]=serviceFlag;
        shellArgs[1]=String.valueOf(signal);
        return new CaseDispatcher().shellRun(PROCESS_RENICE,shellArgs);
    }

    @GetMapping("/system/disk/capacity")
    public Result fillDisk(@RequestParam("path") String path, @RequestParam("bs") String bs, @RequestParam("count") String count){
        Result result = new Result();
        String fillResult = new FaultInject().fillDisk(path, bs, count);
        if(null == fillResult){
            LOGGER.error("Disk Fill FAILED");
            result.setCode(ResultCode.FAIL);
            result.setMessage("Disk Fill FAILED");
            result.setData("");
        }else {
            LOGGER.info("Disk Fill SUCCEED, tmp file:"+fillResult);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("Disk Fill SUCCEED, path:"+path+", bs:"+bs+", count:"+count);
            result.setData(fillResult);
        }
        return result;
    }

    /**
     *  删除临时文件
     * @param file: file绝对路径
     * @return
     */
    @GetMapping("/system/disk/capacity/revoke")
    public Result revokeFillDisk(@RequestParam("file") String file){
        Result result = new Result();

        String fileDelRet = new FaultInject().revokeFillDisk(file);

        if(null == fileDelRet){
            LOGGER.error("Disk Fill revoke FAILED");
            result.setCode(ResultCode.FAIL);
            result.setMessage("Disk Fill revoke FAILED");
            result.setData("");
        }else {
            LOGGER.info("Disk Fill revoke SUCCEED, tmp file:"+file);
            result.setCode(ResultCode.SUCCESS);
            result.setMessage("Disk Fill revoke SUCCEED, file:"+file);
            result.setData(fileDelRet);
        }
        return result;
    }

    @GetMapping("/system/disk/io")
    public Result injectDiskIOBusy(@RequestParam("deviceDir") String deviceDir, @RequestParam("type") String type){
        Result result = new Result();
        ResultCode resultCode = new FaultInject().injectDiskIOBusy(deviceDir, type);

        if(resultCode.getCode().equals(200)) {
            LOGGER.info("Fault Injection SUCCEED");
            result.setCode(resultCode.getCode());
            result.setMessage("Fault Injection SUCCEED.");
            result.setData(deviceDir);
        } else {
            LOGGER.error("Fault Injection FAILED");
            result.setCode(resultCode.getCode());
            result.setMessage("Fault Injection FAILED.");
            result.setData(deviceDir);
        }
        return result;
    }

    @GetMapping("/system/disk/io/revoke")
    public Result revokeDiskIOBusy(@RequestParam("deviceDir") String deviceDir, @RequestParam("type") String type){
        Result result = new Result();
        ResultCode resultCode = new FaultInject().revokeDiskIOBusy(deviceDir, type);
        if(resultCode.getCode().equals(200)) {
            LOGGER.info("Fault Revoke SUCCEED");
            result.setCode(resultCode.getCode());
            result.setMessage("Fault Revoke SUCCEED.");
            result.setData(deviceDir);
        } else {
            LOGGER.error("Fault Revoke FAILED");
            result.setCode(resultCode.getCode());
            result.setMessage("Fault Revoke FAILED.");
            result.setData(deviceDir);
        }
        return result;
    }

    /**
     * 执行一次sql case，并选择性保存case到用例库, case是否批量执行，批量执行并行度设置；
     * @param paramJpson
     */
    @PostMapping(value = "/sql/submit", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void sqlSubmit(@RequestBody JSONObject paramJpson){
        SqlCaseTemplate sqlCaseTemplate = paramJpson.toJavaObject(SqlCaseTemplate.class);
        SqlCase sqlCaseSaved = null;
        boolean saveCase = sqlCaseTemplate.isSaveCase();
        SqlCase sqlCase = sqlCaseTemplate.getSqlCase();
        int parallel = sqlCaseTemplate.getRunOpt().getParallel();
        int loop = sqlCaseTemplate.getRunOpt().getLoop();

        if(saveCase){
            sqlCase.setCreateTimestamp(String.valueOf(System.currentTimeMillis()));
            sqlCase.setUuid(UUID.randomUUID().toString());
            sqlCaseSaved = sqlCaseRepository.save(sqlCase);
            LOGGER.info("# SAVE SQL case succeed,uuid: " + sqlCase.getUuid());
        }
        SqlExecutor.signalSqlRun(sqlCase, parallel,loop);
    }

    /**
     * 支持按照sql_type批量run sql case，设置case执行并行度
     * @param parallel
     * @param type
     */
    @GetMapping("/sql/type/batchrun")
    public void batchRunSameTypeSql(@RequestParam("parallel") Integer parallel, @RequestParam("type") String type){
        List<SqlCase> sqlcases = sqlCaseRepository.findByType(type);
        SqlExecutor.sqlBatchRun(parallel, sqlcases);
    }

    /**
     * 支持按照case库中前num数sqlcase 批量执行，可设置并行度，可多批次执行；
     * @param parallel
     * @param num
     */
    @GetMapping("/sql/rnd/batchrun")
    public void batchRunRndTypeSql(@RequestParam("parallel") Integer parallel, @RequestParam("num") Integer num, @RequestParam("loop") Integer loop){
        List<SqlCase> sqlCases = sqlCaseRepository.findHeadN(num);
        for(int i=0;i<loop;i++){
            SqlExecutor.sqlBatchRun(parallel, sqlCases);
        }
    }


}
