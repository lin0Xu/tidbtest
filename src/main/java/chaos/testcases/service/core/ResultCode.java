package chaos.testcases.service.core;

public enum ResultCode {
    SUCCESS(200),//成功
    FAIL(400),//失败
    UNEXPECTED(1000);//内部错误

    private Integer code;

    ResultCode(Integer code) {
        this.code = code;
    }

    public Integer getCode(){
        return this.code;
    }
}

