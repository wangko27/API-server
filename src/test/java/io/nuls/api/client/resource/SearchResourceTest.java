package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientSearchResult;
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
        String keyword = "2CVxEw3XJXwc2H5Ue7FYn82XDEJ2Wbm";
        RpcClientSearchResult result = (RpcClientSearchResult) this.util.get("/search/" + keyword, null);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }
}
