package io.nuls.api.server.resources.impl;

import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.AccountBalanceReportBusiness;
import io.nuls.api.server.business.AliasBusiness;
import io.nuls.api.server.business.MinedReportBusiness;
import io.nuls.api.entity.Alias;
import io.nuls.api.entity.AliasParam;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/report")
@Component
public class ReportResource {

    @Autowired
    private MinedReportBusiness minedReportBusiness;
    @Autowired
    private AccountBalanceReportBusiness accountBalanceReportBusiness;
    @Autowired
    private AliasBusiness aliasBusiness;

    @GET
    @Path("/balance")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult balance(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
        RpcClientResult result;
        try {
            AliasParam aliasParam = new AliasParam();
            AliasParam.Criteria criteria = aliasParam.createCriteria();
            criteria.andAddressIsNotNull();
            List<Alias> aliases = aliasBusiness.selectAliasList(aliasParam);
            result = RpcClientResult.getSuccess();
            result.setData(aliases);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/mined")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult mined(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
        RpcClientResult result;
        try {
            //AliasParam aliasParam = new AliasParam();
            //AliasParam.Criteria criteria = aliasParam.createCriteria();
            //criteria.andAddressIsNotNull();
            //List<Alias> aliases = aliasBusiness.selectAliasList(aliasParam);
            System.out.println("================test client function request====================");
            result = RpcClientResult.getSuccess();
            result.setData(null);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

}
