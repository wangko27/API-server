package io.nuls.api.client.resource;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.RestFulUtils;
import org.junit.*;
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
        //serverUri = "http://192.168.1.223:8765/nuls";
        serverUri = "http://127.0.0.1:8765/nuls";
        util = RestFulUtils.getInstance();
        util.init(serverUri);
    }

    @Test
    public void loadTest(){
        RpcClientResult result = this.util.get("/tx/hash/000020ed62d53b0e3b5aa791a720c672e7902252b46201dc764aa91869ae56d8d45cf5",null);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void spentTest(){
        String txHash="000020b1939128abe321e132f57813a87ed662c8d5c8e9cc3e526586b19965ee5b04cd";
        String index = "1";
        Map<String, String> params = new HashMap<>(2);
        params.put("txHash", txHash);
        params.put("index", String.valueOf(index));
        RpcClientResult result = this.util.get("/tx/bySpent",params);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }


    @Test
    public void listTest(){
        Integer pageSize = 10;
        Integer pageNumber = 1;
//        String address = "2CdYovbhsiKGW18HRNiFuLQva1Voz6i";
        String address = "2CaWijtZQaUVayV2c5Yd1MRjyWWQugv";
        //int type = 1;
        int blockHeight = 0;
        Map<String, String> param = new HashMap<>();
        //param.put("address", address);
        param.put("blockHeight", String.valueOf(blockHeight));
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        //param.put("type", String.valueOf(type));
        RpcClientResult result = this.util.get("/tx/list",param);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }


    @Test
    public void lockedTest(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        String address = "2CVxEw3XJXwc2H5Ue7FYn82XDEJ2Wbm";
        Map<String, String> param = new HashMap<>();
        param.put("address", address);
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        RpcClientResult result = this.util.get("/tx/utxo/locked",param);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

}
