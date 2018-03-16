package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * @author: Charlie
 * @date: 2018/3/15
 */
public class AccountResourceTest {

    private RestFulUtils util;
    private String serverUri;
    private Client client;

    @Before
    public void init(){
        client = ClientBuilder.newClient();
        serverUri = "http://192.168.1.223:8765/nuls";
        //serverUri = "http://127.0.0.1:8001/nuls";
        this.util = RestFulUtils.getInstance();
        this.util.init(serverUri);
    }

    @Test
    public void accountTest(){
        RpcClientResult result = this.util.get("/account/2CdYovbhsiKGW18HRNiFuLQva1Voz6i", null);
        System.out.println(result.toString());
    }

    @Test
    public void balanceTest(){
        RpcClientResult result = this.util.get("/account/balance/2CdYovbhsiKGW18HRNiFuLQva1Voz6i", null);
        System.out.println(result.toString());
    }

    @After
    public void stop(){
        System.out.println("end...");
    }
}
