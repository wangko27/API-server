package io.nuls.main;

import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.server.ApiApplication;
import io.nuls.api.constant.RpcConstant;
import io.nuls.api.utils.PropertiesUtils;
import io.nuls.api.utils.StringUtils;

public enum Main {
    MAIN;

    private String ip;
    private String port;
    private String moduleUrl;
    private String remoteIp;
    private String remotePort;
    private String remoteModuleUrl;
    private ApiApplication entry;

    public void init() {
        this.ip = PropertiesUtils.readProperty(RpcConstant.CFG_RPC_SERVER_IP);
        this.port = PropertiesUtils.readProperty(RpcConstant.CFG_RPC_SERVER_PORT);
        this.moduleUrl = PropertiesUtils.readProperty(RpcConstant.CFG_RPC_SERVER_URL);
        this.remoteIp = PropertiesUtils.readProperty(RpcConstant.CFG_RPC_REMOTE_SERVER_IP);
        this.remotePort = PropertiesUtils.readProperty(RpcConstant.CFG_RPC_REMOTE_SERVER_PORT);
        this.remoteModuleUrl = PropertiesUtils.readProperty(RpcConstant.CFG_RPC_REMOTE_SERVER_URL);
        this.entry = ApiApplication.ENTRY;
    }

    public void startUp() {
        String serverUri;
        if (StringUtils.isBlank(ip) || StringUtils.isBlank(port)) {
            entry.start(RpcConstant.DEFAULT_IP, RpcConstant.DEFAULT_PORT, RpcConstant.DEFAULT_URL);
        } else {
            entry.start(ip, Integer.parseInt(port), moduleUrl);
        }

        if (StringUtils.isBlank(remoteIp) || StringUtils.isBlank(remotePort)) {
            serverUri = RpcConstant.DEFAULT_REMOTE_SERVER;
        } else {
            serverUri = RpcConstant.HTTP + remoteIp + RpcConstant.COLON + Integer.parseInt(remotePort) + RpcConstant.URI_SEPARATOR + remoteModuleUrl;
        }
        RestFulUtils.getInstance().init(serverUri);
    }

    public static void main(String[] args) {
        Main.MAIN.init();
        Main.MAIN.startUp();
    }
}
