package chaos.testcases.service.service;

import chaos.testcases.service.model.SqlCase;
import java.util.List;

public interface SqlCaseService {
    SqlCase findOne(String uuid);
    List<SqlCase> FindByType(String type);
}
