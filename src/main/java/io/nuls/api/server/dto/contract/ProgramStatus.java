package io.nuls.api.server.dto.contract;

public enum ProgramStatus {

    /**
     * 1-未找到、2-正常、3-停止
     */
    NOT_FOUND("not_found", 1), NORMAL("normal", 2), STOP("stop", 3);

    //状态描述
    private String name;
    //状态编码
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
