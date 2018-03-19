package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2018/3/15
 */
public class AccountResourceTest {

    private RestFulUtils util;
    private String serverUri;

    @Before
    public void init(){
        serverUri = "http://192.168.1.223:8765/nuls";
        //serverUri = "http://127.0.0.1:8001/nuls";
        this.util = RestFulUtils.getInstance();
        this.util.init(serverUri);
    }

    @Test
    public void accountTest(){
        RpcClientResult result = this.util.get("/account/2CYbFsnYkfry3uQS3Cx7pxSybhQP5hu", null);
        System.out.println(result.toString());
    }

    @Test
    public void utxoTest(){
        String address = "2CYbFsnYkfry3uQS3Cx7pxSybhQP5hu";
        long amount = 10000000;
        Map<String, String> param = new HashMap<>(2);
        param.put("address", address);
        param.put("amount", String.valueOf(amount));
        RpcClientResult result = this.util.get("/account/utxo", param);
        System.out.println(result.toString());
    }

}
