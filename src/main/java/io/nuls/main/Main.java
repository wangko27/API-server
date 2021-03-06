package io.nuls.main;

import io.nuls.api.constant.Constant;
import io.nuls.api.i18n.I18nUtils;
import io.nuls.api.server.ApiApplication;
import io.nuls.api.server.task.AssetsBrowseTask;
import io.nuls.api.utils.PropertiesUtils;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;

public enum Main {
    MAIN;

    private String ip;
    private String port;
    private String moduleUrl;
    private String remoteIp;
    private String remotePort;
    private String remoteModuleUrl;
    private String language;
    private ApiApplication entry;

    public void init() {
        this.ip = PropertiesUtils.readProperty(Constant.CFG_RPC_SERVER_IP);
        this.port = PropertiesUtils.readProperty(Constant.CFG_RPC_SERVER_PORT);
        this.moduleUrl = PropertiesUtils.readProperty(Constant.CFG_RPC_SERVER_URL);
        this.remoteIp = PropertiesUtils.readProperty(Constant.CFG_RPC_REMOTE_SERVER_IP);
        this.remotePort = PropertiesUtils.readProperty(Constant.CFG_RPC_REMOTE_SERVER_PORT);
        this.remoteModuleUrl = PropertiesUtils.readProperty(Constant.CFG_RPC_REMOTE_SERVER_URL);
        this.language = PropertiesUtils.readProperty(Constant.CFG_SYSTEM_LANGUAGE);
        AssetsBrowseTask.businessAddress = PropertiesUtils.readProperty(Constant.BUSINESS_ADDRESS);
        AssetsBrowseTask.teamAddress = PropertiesUtils.readProperty(Constant.TEAM_ADDRESS);
        AssetsBrowseTask.communityAddress = PropertiesUtils.readProperty(Constant.COMMUNITY_ADDRESS);
        AssetsBrowseTask.mappingAddress = PropertiesUtils.readProperty(Constant.MAPPING_ADDRESS).split(",");
        this.entry = ApiApplication.ENTRY;
    }

    public void startUp() throws Exception {
        String serverUri;
        if (StringUtils.isBlank(remoteIp) || StringUtils.isBlank(remotePort)) {
            serverUri = Constant.DEFAULT_REMOTE_SERVER;
        } else {
            if(StringUtils.isBlank(remoteModuleUrl)) {
                serverUri = Constant.HTTP + remoteIp + Constant.COLON + Integer.parseInt(remotePort);
            } else {
                serverUri = Constant.HTTP + remoteIp + Constant.COLON + Integer.parseInt(remotePort) + Constant.URI_SEPARATOR + remoteModuleUrl;
            }
        }

        RestFulUtils.getInstance().init(serverUri);
        if(StringUtils.isBlank(language)) {
            language = Constant.DEFAULT_LANGUAGE;
        }
        I18nUtils.setLanguage(language);

        if (StringUtils.isBlank(ip) || StringUtils.isBlank(port)) {
            entry.start(Constant.DEFAULT_IP, Constant.DEFAULT_PORT, Constant.DEFAULT_URL);
        } else {
            entry.start(ip, Integer.parseInt(port), moduleUrl);
        }
        //启动sdk
        //SDKBootstrap.init(this.remoteIp,this.remotePort);
    }

    public static void main(String[] args) throws Exception {
        Main.MAIN.init();
        Main.MAIN.startUp();
    }
}
