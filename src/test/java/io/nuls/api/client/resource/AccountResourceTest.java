package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2018/3/15
 */
public class AccountResourceTest {

    private static RestFulUtils util;
    private static String serverUri;

    @BeforeClass
    public static void init(){
        //serverUri = "http://192.168.1.223:8765/nuls";
        serverUri = "http://127.0.0.1:8765/nuls";
        util = RestFulUtils.getInstance();
        util.init(serverUri);
    }

    @Test
    public void accountTest(){
        RpcClientResult result = this.util.get("/account/2CYbFsnYkfry3uQS3Cx7pxSybhQP5hu", null);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
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
        Assert.assertTrue(result.isSuccess());
    }

}
