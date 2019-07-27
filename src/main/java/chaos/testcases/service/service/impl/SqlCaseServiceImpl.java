package chaos.testcases.service.service.impl;

import chaos.testcases.service.model.SqlCase;
import chaos.testcases.service.repository.SqlCaseRepository;
import chaos.testcases.service.service.SqlCaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SqlCaseServiceImpl implements SqlCaseService {

    @Autowired
    private SqlCaseRepository sqlCaseRepository;

    public SqlCase findOne(String uuid){
        SqlCase sqlCase = sqlCaseRepository.findOne(uuid);
        return sqlCase;
    }
    public List<SqlCase> FindByType(String type){
        return sqlCaseRepository.FindByType(type);
    }
}
