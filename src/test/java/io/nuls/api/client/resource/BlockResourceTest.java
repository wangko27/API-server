package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2018/3/16
 */
public class BlockResourceTest {
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
    public void loadBlockTest(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/hash/000020585b478c2a912ced102875d5f8ceeb36b04f5e1e825b00c13c368e87a0bcc147",null);
        System.out.println(result.toString());
    }

    @Test
    public void getBlockTest(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/height/w5",null);
        System.out.println(result.toString());
    }

    @Test
    public void newest(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/newest",null);
        System.out.println(result.toString());
    }


    @Test
    public void getHeaderByHeight(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/header/height/9",null);
        System.out.println(result.toString());
    }

    @Test
    public void getHeader(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/header/hash/000020585b478c2a912ced102875d5f8ceeb36b04f5e1e825b00c13c368e87a0bcc147",null);
        System.out.println(result.toString());
    }

    @Test
    public void getList(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        Map<String, String> param = new HashMap<>();
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        RpcClientResult result = RestFulUtils.getInstance().get("/block/list",param);
        System.out.println(result.toString());
    }

    @Test
    public void getListByAddress(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        String address = "2ChegErt2tJLC7Pp62p1wrqPz49w3Xo";
        int type = 1;
        Map<String, String> param = new HashMap<>();
        param.put("address",address);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        param.put("type", String.valueOf(type));
        RpcClientResult result = RestFulUtils.getInstance().get("/block/list/address",param);
        System.out.println(result.toString());
    }
}