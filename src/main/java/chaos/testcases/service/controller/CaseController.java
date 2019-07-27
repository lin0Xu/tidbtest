package chaos.testcases.service.controller;

import chaos.testcases.service.tools.CaseDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static chaos.testcases.service.core.BaseData.*;

@RestController
@RequestMapping("/case")
public class CaseController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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


}
