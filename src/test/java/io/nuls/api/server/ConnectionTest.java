package io.nuls.api.server;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.i18n.I18nUtils;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.log.Log;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class ConnectionTest {
    private ApiApplication serverService;
    private String serverUri;
    private Client client;

    @Before
    public void init() throws Exception {
        client = ClientBuilder.newClient();
        serverUri = "http://127.0.0.1:6666/test";
        RestFulUtils.getInstance().init(serverUri);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverService = ApiApplication.ENTRY;
                serverService.start("127.0.0.1", 6666, "test");
            }
        });
        thread.start();
    }

    @Test
    public void connectionTest() {
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RpcClientResult result = get("/", null);
        Log.debug(result.toString());
        Assert.assertEquals("Success", result.getMsg());
    }

    private RpcClientResult get(String path, Map<String, String> params) {
        if (null == serverUri) {
            throw new RuntimeException("service url is null");
        }
        WebTarget target = client.target(serverUri).path(path);
        if (null != params && !params.isEmpty()) {
            for (String key : params.keySet()) {
                target.queryParam(key, params.get(key));
            }
        }
        return target.request(APPLICATION_JSON).get(RpcClientResult.class);
    }

    @After
    public void stop() {
        serverService.shutdown();
    }
}
