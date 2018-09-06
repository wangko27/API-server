package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AliasBusiness;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.TransactionBusiness;
import io.nuls.api.server.business.TransactionRelationBusiness;
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
 * @author: Charlie
 * @date: 2018/4/2
 */
@Path("/search")
@Component
public class SearchResource {

    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private AliasBusiness aliasBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private TransactionRelationBusiness transactionRelationBusiness;


    @GET
    @Path("/{keyword}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult search(@PathParam("keyword") String keyword) {
        RpcClientResult result = null;
        result = RpcClientResult.getSuccess();
        result.setData(0);
        if (StringUtils.isBlank(keyword)) {
            return result;
        }
        keyword = keyword.trim();
        try {
            if (StringUtils.isNonNegativeInteger(keyword)) {
                //高度
                if (null != blockBusiness.getByKey(Long.valueOf(keyword + ""))) {
                    result.setData(EntityConstant.SEARCH_HEADER_HEIGHT);
                }
            } else if (StringUtils.validAddress(keyword)) {
                if (transactionRelationBusiness.isAddressExist(keyword) > 0) {
                    result.setData(EntityConstant.SEARCH_ACCOUNT_ADDRESS);
                }
            } else if (StringUtils.validHash(keyword)) {
                if (null != blockBusiness.getBlockByHash(keyword)) {
                    result.setData(EntityConstant.SEARCH_HEADER_HASH);
                } else if (null != transactionBusiness.getByHash(keyword)) {
                    result.setData(EntityConstant.SEARCH_TX_HASH);
                }
            }/*else{
                //不是高度，不是地址，不是hash，根据别名搜索    根据别名获取地址
                Alias alias = aliasBusiness.getAliasByAlias(keyword);
                if(null != alias && StringUtils.isNotBlank(alias.getAddress())){

                }
            }*/
        } catch (Exception e) {
            Log.error(e);
        }
        return result;
    }
}
