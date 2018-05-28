package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.SearchCode;
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
//@Path("/search")
@Component
public class SearchResource {

//    @GET
//    @Path("/{keyword}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public RpcClientSearchResult search(@PathParam("keyword") String keyword){
//        RpcClientResult resultTemp;
//        RpcClientSearchResult result;
//        try {
//            if(StringUtils.isNonNegativeInteger(keyword)){
//                resultTemp = RestFulUtils.getInstance().get("/block/height/" + keyword, null);
//                result = new RpcClientSearchResult(resultTemp, SearchCode.HEADER_HEIGHT.getCode());
//            } else if (StringUtils.validAddress(keyword)){
//                RpcClientResult resultInfo = RestFulUtils.getInstance().get("/account/balance/" + keyword, null);
//                if(!resultInfo.isSuccess()){
//                    return (RpcClientSearchResult)RpcClientResult.getFailed();
//                }
//                resultTemp = RestFulUtils.getInstance().get("/account/" + keyword, null);
//                if(resultTemp.isSuccess() && null != resultTemp.getData()) {
//                    Map map = (Map) resultInfo.getData();
//                    map.putAll((Map)resultTemp.getData());
//                    resultInfo.setData(map);
//                }
//                result = new RpcClientSearchResult(resultInfo, SearchCode.ACCOUNT_ADDRESS.getCode());
//            } else if(StringUtils.validHash(keyword)){
//                resultTemp =  RestFulUtils.getInstance().get("/tx/hash/"+keyword, null);
//                if(resultTemp.isSuccess()){
//                    result = new RpcClientSearchResult(resultTemp, SearchCode.TX_HASH.getCode());
//                }else{
//                    resultTemp = RestFulUtils.getInstance().get("/block/header/hash/"+keyword, null);
//                    if(resultTemp.isSuccess()){
//                        result = new RpcClientSearchResult(resultTemp, SearchCode.HEADER_HASH.getCode());
//                    }else{
//                        result = (RpcClientSearchResult)RpcClientResult.getFailed();
//                    }
//                }
//            } else {
//                result = (RpcClientSearchResult)RpcClientSearchResult.getFailed();
//            }
//        } catch (Exception e) {
//            result = (RpcClientSearchResult)RpcClientResult.getFailed();
//            Log.error(e);
//        }
//        return result;
//    }
}
