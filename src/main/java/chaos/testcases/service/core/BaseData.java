package chaos.testcases.service.core;

public class BaseData {
    public static final String PROCESS_KILL = "src/main/shell/process/ps_kill_via_flag.sh";
    public static final String PROCESS_RECOVER_VIA_CMD = "src/main/shell/process/ps_kill_via_pid.sh";
    public static final String PROCESS_RENICE = "src/main/shell/process/ps_renice.sh";

    public static final String DST_DB_CON_STR = "jdbc:mysql://localhost:4000?user=tidb&password=tidb";
    public static final Integer MAX_SQL_RUN_PARALLEL = 100;
}
