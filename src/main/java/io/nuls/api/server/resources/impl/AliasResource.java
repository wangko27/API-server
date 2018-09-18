package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.entity.Alias;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AliasBusiness;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Description:别名相关
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("alias")
@Component
public class AliasResource {

    @Autowired
    private AliasBusiness aliasBusiness;

    /**
     * 根据地址获取别名
     * @param address 地址
     * @return
     */
    @GET
    @Path("/get/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getAlias(@PathParam("address") String address){
        RpcClientResult result = null;
        if (!StringUtils.validAddress(address)) {
            result = RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
            return result;
        }
        try {
            result = RpcClientResult.getSuccess();
            Alias alias = aliasBusiness.getAliasByAddress(address);
            //处理掉null返回
            if(null == alias){
                alias = new Alias();
            }
            result.setData(alias);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }
}
