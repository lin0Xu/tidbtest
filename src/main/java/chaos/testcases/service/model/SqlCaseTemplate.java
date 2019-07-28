package chaos.testcases.service.model;

public class SqlCaseTemplate {

    private boolean saveCase;
    private SqlCase sqlCase;
    private RunOpt runOpt;

    public SqlCaseTemplate(){}

    public boolean isSaveCase() {
        return saveCase;
    }

    public void setSaveCase(boolean saveCase) {
        this.saveCase = saveCase;
    }

    public SqlCase getSqlCase() {
        return sqlCase;
    }

    public void setSqlCase(SqlCase sqlCase) {
        this.sqlCase = sqlCase;
    }

    public RunOpt getRunOpt() {
        return runOpt;
    }

    public void setRunOpt(RunOpt runOpt) {
        this.runOpt = runOpt;
    }
}
