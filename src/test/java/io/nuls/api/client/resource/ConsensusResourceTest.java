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
 * @date: 2018/3/19
 */
public class ConsensusResourceTest {
    private static RestFulUtils util;
    private static String serverUri;

    @BeforeClass
    public static void init(){
        serverUri = "http://127.0.0.1:8765/nuls";
        util = RestFulUtils.getInstance();
        util.init(serverUri);
    }

    @Test
    public void queryAgentTest(){
        String agentAddress = "2CdYovbhsiKGW18HRNiFuLQva1Voz6i";
        RpcClientResult result = this.util.get("/consensus/agent/" + agentAddress, null);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void queryAgentListTest(){
        Integer pageSize = 20;
        Integer pageNumber = 1;
        //String keyword = "keyword";
        //String sort = "address";
        Map<String, String> param = new HashMap<>();
        param.put("pageNumber", String.valueOf(pageNumber));
        param.put("pageSize", String.valueOf(pageSize));
        RpcClientResult result = this.util.get("/consensus/agent/list", param);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void queryAllOfAgentStatusTest(){
        RpcClientResult result = this.util.get("/consensus/agent/status", null);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void queryAllAgentStatisticsTest(){
        RpcClientResult result = this.util.get("/consensus", null);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void queryDepositByAddressTest(){
        String address = "2CdYovbhsiKGW18HRNiFuLQva1Voz6i";
        String agentAddress = "";
        Integer pageSize = 20;
        Integer pageNumber = 1;
        Map<String, String> param = new HashMap<>();
        param.put("address", address);
        //param.put("pageNumber", String.valueOf(pageNumber));
        //param.put("pageSize", String.valueOf(pageSize));
        RpcClientResult result = this.util.get("/consensus/deposit/address/"+ address, param);
        System.out.println(result.toString());
        Assert.assertTrue(result.isSuccess());
    }
}
