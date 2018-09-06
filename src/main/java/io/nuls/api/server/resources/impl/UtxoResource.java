package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.TransactionBusiness;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.dto.TransactionDto;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Path("utxo")
@Component
public class UtxoResource {

    @Autowired
    private UtxoBusiness utxoBusiness;

    @GET
    @Path("/list/address")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult listWithAddress(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("address")String address){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            return result;
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        result = RpcClientResult.getSuccess();
        result.setData(utxoBusiness.getListByAddress(address,pageNumber,pageSize));
        return result;
    }
}
