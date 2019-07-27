package chaos.testcases.service.model;

import javax.persistence.*;


@Entity
@Table(name="sql_case")
public class SqlCase {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(name="uuid")
    private String uuid;

    @Column(name="sql")
    private String sql;

    @Column(name="sql_type")
    private String sql_type;

    @Column(name="description")
    private String description;

    @Column(name="create_timestamp")
    private String create_timestamp;

    public SqlCase(){}

    public Integer getId() {
        return id;
    }
    public String getSQL() {
        return sql;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public void setSql(String sql) {
        this.sql = sql;
    }
}
