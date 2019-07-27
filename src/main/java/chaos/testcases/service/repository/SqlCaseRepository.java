package chaos.testcases.service.repository;

import chaos.testcases.service.model.SqlCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SqlCaseRepository extends JpaRepository<SqlCase,String> {

    @Query(value = "select * from sql_case where uuid=?1", nativeQuery = true)
    SqlCase findOne(String uuid);

    @Query(value = "select * from sql_case where sql_type=?1", nativeQuery = true)
    List<SqlCase> FindByType(String type);
}
