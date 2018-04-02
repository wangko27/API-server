package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.constant.SearchCode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.RpcClientSearchResult;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2018/4/2
 */
@Path("/search")
@Component
public class SearchResource {

    @GET
    @Path("/{keyword}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult search(@PathParam("keyword") String keyword){
        RpcClientResult resultTemp;
        RpcClientResult result;
        try {
            if(StringUtils.isNonNegativeInteger(keyword)){
                //数字
                resultTemp = RestFulUtils.getInstance().get("/block/height/" + keyword, null);
                result = new RpcClientSearchResult(resultTemp, SearchCode.HEADER_HEIGHT.getCode());
            } else if (StringUtils.validAddress(keyword)){
                //地址
                resultTemp = RestFulUtils.getInstance().get("/account/" + keyword, null);
                if(!resultTemp.isSuccess()){
                    return RpcClientResult.getFailed();
                }
                RpcClientResult resultBalance = RestFulUtils.getInstance().get("/account/balance/" + keyword, null);
                if(!resultBalance.isSuccess()){
                    return RpcClientResult.getFailed();
                }
                Map map = (Map)resultTemp.getData();
                map.putAll((Map)resultBalance.getData());
                resultTemp.setData(map);
                result = new RpcClientSearchResult(resultTemp, SearchCode.ACCOUNT_ADDRESS.getCode());
            } else if(StringUtils.validHash(keyword)){
                resultTemp =  RestFulUtils.getInstance().get("/tx/hash/"+keyword, null);
                if(resultTemp.isSuccess()){
                    result = new RpcClientSearchResult(resultTemp, SearchCode.TX_HASH.getCode());
                }else{
                    resultTemp = RestFulUtils.getInstance().get("/block/header/hash/"+keyword, null);
                    if(resultTemp.isSuccess()){
                        result = new RpcClientSearchResult(resultTemp, SearchCode.HEADER_HASH.getCode());
                    }else{
                        result = RpcClientResult.getFailed();
                    }
                }
            } else {
                result = RpcClientResult.getFailed();
            }
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
