package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

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
        RpcClientResult result = RestFulUtils.getInstance().get("/block/hash/0000202cf0fdc799312730d974a9153414fc4f1fb93bcba8038518425ae0a695c8054b",null);
        System.out.println(result.toString());
    }

    @Test
    public void getBlockTest(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/height/0",null);
        System.out.println(result.toString());
    }

    @Test
    public void getBestBlockHeight(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/bestheight",null);
        System.out.println(result.toString());
    }

    @Test
    public void getBestBlockHash(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/bestheight",null);
        System.out.println(result.toString());
    }

    @Test
    public void getHeaderByHeight(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/header/height/9",null);
        System.out.println(result.toString());
    }

    @Test
    public void getHeader(){
        RpcClientResult result = RestFulUtils.getInstance().get("/block/header/hash/0000202cf0fdc799312730d974a9153414fc4f1fb93bcba8038518425ae0a695c8054b",null);
        System.out.println(result.toString());
    }
}
