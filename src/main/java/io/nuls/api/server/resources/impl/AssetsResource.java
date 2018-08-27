package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.NulsStatistics;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AssetsBusiness;
import io.nuls.api.server.dto.NulsStatisticsDto;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/**
 * Created by inchain on 2018/8/23.
 */
@Path("/assets")
@Component
public class AssetsResource {
    @Autowired
    private AssetsBusiness assetsBusiness;
    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getAssets() {
        RpcClientResult result = null;
        try {
            result = RpcClientResult.getSuccess();
            NulsStatistics nulsStatistics = assetsBusiness.get(Constant.TOKEN_CACHE_KEY);
            if(null == nulsStatistics) {
                result.setData(new NulsStatisticsDto());
            } else {
                result.setData(new NulsStatisticsDto(nulsStatistics));
            }
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}