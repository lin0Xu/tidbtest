package chaos.testcases.service.controller;

import chaos.testcases.service.model.SqlCase;
import chaos.testcases.service.model.SqlCaseTemplate;
import chaos.testcases.service.repository.SqlCaseRepository;
import chaos.testcases.service.tools.CaseDispatcher;
import chaos.testcases.service.tools.SqlExecutor;
import chaos.testcases.service.tools.TransactionExecutor;
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
     * 支持按照case库中前num数sqlcase 批量执行，可设置并行度
     * @param parallel
     * @param num
     */
    @GetMapping("/sql/rnd/batchrun")
    public void batchRunRndTypeSql(@RequestParam("parallel") Integer parallel, @RequestParam("num") Integer num){
        List<SqlCase> sqlCases = sqlCaseRepository.findHeadN(num);
        SqlExecutor.sqlBatchRun(parallel, sqlCases);
    }


}
