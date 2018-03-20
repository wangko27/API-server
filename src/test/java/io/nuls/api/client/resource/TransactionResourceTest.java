package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: lichao
 * @date: 2018/03/15
 */
public class TransactionResourceTest {
    private static RestFulUtils util;
    private static String serverUri;

    @BeforeClass
    public static void init(){
        serverUri = "http://192.168.1.223:8765/nuls";
        //serverUri = "http://127.0.0.1:8001/nuls";
        util = RestFulUtils.getInstance();
        util.init(serverUri);
    }

    @Test
    public void loadTest(){
        RpcClientResult result = this.util.get("/tx/hash/0000205af03534f8ddc28a6918f3d0c4d3c57d41abca067589b9d70ce6e2c546a42f2f",null);
        System.out.println(result.toString());
    }

    @Test
    public void listTest(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        String address = "2CdYovbhsiKGW18HRNiFuLQva1Voz6i";
        int type = 0;
        Map<String, String> param = new HashMap<>();
        param.put("address", address);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        //param.put("type", String.valueOf(type));
        RpcClientResult result = this.util.get("/tx/list",param);
        System.out.println(result.toString());
    }

    @Test
    public void blockTest(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        String height = "0";
        Map<String, String> param = new HashMap<>();
        param.put("height",height);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        RpcClientResult result = this.util.get("/tx/block/list",param);
        System.out.println(result.toString());
    }

    @Test
    public void lockedTest(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        String address = "2CdYovbhsiKGW18HRNiFuLQva1Voz6i";
        Map<String, String> param = new HashMap<>();
        param.put("address", address);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        RpcClientResult result = this.util.get("/tx/utxo/locked",param);
        System.out.println(result.toString());
    }

}
