package chaos.testcases.service.core;

public class BaseData {
    public static final String PROCESS_KILL = "src/main/shell/process/ps_kill_via_flag.sh";
    public static final String PROCESS_RECOVER_VIA_CMD = "src/main/shell/process/ps_kill_via_pid.sh";
    public static final String PROCESS_RENICE = "src/main/shell/process/ps_renice.sh";

//    public static final String DST_DB_CON_STR = "jdbc:mysql://localhost?user=root";
    public static final String DST_DB_CON_STR = "jdbc:mysql://localhost:4000?user=tidb&password=tidb";
    public static final Integer MAX_SQL_RUN_PARALLEL = 100;

    public static final String TRANSACTION_SQL_CASE_FLAG = "transaction_sql";

    public static final String DISK_CAPACITY_FILL = "src/main/shell/disk/disk_capacity_fill.sh";
    public static final String DISK_CAPACITY_FILL_REVOKE = "src/main/shell/disk/disk_capacity_fill_revoke.sh";
    public static final String DISK_IO_BUSY_INJECT = "src/main/shell/disk/disk_io_busy.sh";
    public static final String DISK_IO_BUSY_REVOKE = "src/main/shell/disk/disk_io_busy_revoke.sh";

}
