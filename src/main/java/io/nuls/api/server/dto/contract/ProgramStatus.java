package io.nuls.api.server.dto.contract;

public enum ProgramStatus {

    NOT_FOUND("not_found", 1), NORMAL("normal", 2), STOP("stop", 3);
    // 成员变量
    private String name;
    private int code;

    ProgramStatus(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static ProgramStatus codeOf(String name){
        for(ProgramStatus status : values()){
            if(status.getName().equals(name)){
                return status;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }
}
