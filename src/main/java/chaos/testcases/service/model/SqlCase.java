package chaos.testcases.service.model;

import javax.persistence.*;


@Entity
@Table(name = "sql_case")
public class SqlCase {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "sql_val")
    private String sqlValue;

    @Column(name = "sql_type")
    private String sqlType;

    @Column(name = "description")
    private String description;

    @Column(name = "create_timestamp")
    private String createTimestamp;

    public SqlCase(){}

    public SqlCase(String uuid, String createTimestamp){
        this.uuid = uuid;
        this.createTimestamp = createTimestamp;
    }


    public String getSqlValue() {
        return sqlValue;
    }

    public void setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(String createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
}