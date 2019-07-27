package chaos.testcases.service.controller;

import chaos.testcases.service.model.SqlCase;
import chaos.testcases.service.repository.SqlCaseRepository;
import chaos.testcases.service.tools.CaseDispatcher;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/sql/case/run", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public void inject(@RequestBody JSONObject sqlCaseJson){
        SqlCase sqlCase = sqlCaseJson.toJavaObject(SqlCase.class);
        sqlCase.setCreateTimestamp(String.valueOf(System.currentTimeMillis()));
        sqlCase.setUuid(UUID.randomUUID().toString());

        sqlCaseRepository.save(sqlCase);
        LOGGER.info("# SAVE SQL case succeed,uuid: " + sqlCase.getUuid());
    }


}
