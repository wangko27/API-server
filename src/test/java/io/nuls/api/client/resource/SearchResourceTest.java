package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author: Charlie
 * @date: 2018/4/2
 */
public class SearchResourceTest {
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
    public void searchTest(){
        String keyword = "0000200999dd070a99fb9710688786b411d4dd058301c7663f20bd9dbce1c44a755e92";
        RpcClientResult result = this.util.get("/search/" + keyword, null);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }
}
