package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: lichao
 * @date: 2018/03/15
 */
public class TransactionResourceTest {
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
    public void loadTest(){
        RpcClientResult result = this.util.get("/tx/hash/0000205af03534f8ddc28a6918f3d0c4d3c57d41abca067589b9d70ce6e2c546a42f2f",null);
        System.out.println(result.toString());
    }

    @Test
    @Ignore
    public void listTest(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        String address = "2CdYovbhsiKGW18HRNiFuLQva1Voz6i";
        int type = 1;
        Map<String, String> param = new HashMap<>();
        param.put("address", address);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        param.put("type", String.valueOf(type));

        RpcClientResult result = this.util.get("/tx/address/list",param);
        System.out.println(result.toString());
    }

    @After
    public void stop(){
        System.out.println("end...");
    }

}
