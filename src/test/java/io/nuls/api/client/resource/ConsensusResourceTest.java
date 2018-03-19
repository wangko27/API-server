package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: Charlie
 * @date: 2018/3/19
 */
public class ConsensusResourceTest {
    private RestFulUtils util;
    private String serverUri;

    @Before
    public void init(){
        serverUri = "http://192.168.1.223:8765/nuls";
        this.util = RestFulUtils.getInstance();
        this.util.init(serverUri);
    }

    @Test
    public void infoTest(){
        RpcClientResult result = this.util.get("/consensus/info/2ChegErt2tJLC7Pp62p1wrqPz49w3Xo", null);
        System.out.println(result.toString());
    }
}
