package io.nuls.api.server.resources.impl;

import io.nuls.api.entity.*;
import io.nuls.api.server.business.*;
import io.nuls.api.server.dto.Page;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
@Component
public class ReportResource {

    @Autowired
    private BalanceTopBusiness balanceTopBusiness;
    @Autowired
    private MinedTopBusiness minedTopBusiness;
    @Autowired
    private TxHistoryBusiness txHistoryBusiness;

    @GET
    @Path("/address/balancelist")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult balance(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
        RpcClientResult result;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed();
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
        try {
            Page<BalanceTop> page = new Page<>();
            page.setPageNumber(pageNumber);
            page.setPageSize(pageSize);
            page.setTotal(balanceTopBusiness.countBalanceTopList(null));
            BalanceTopParam balanceTopParam = new BalanceTopParam();
            balanceTopParam.setOrderByClause("id asc");
            balanceTopParam.setStart(page.getStart());
            balanceTopParam.setCount(pageSize);
            List<BalanceTop> list = balanceTopBusiness.selectBalanceTopList(balanceTopParam);
            page.setList(list);
            result = RpcClientResult.getSuccess();
            result.setData(page);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/address/minedlist")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult mined(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize) {
        RpcClientResult result;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed();
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
        try {
            Page<MinedTop> page = new Page<>();
            page.setPageNumber(pageNumber);
            page.setPageSize(pageSize);
            page.setTotal(minedTopBusiness.countMinedTopList(null));
            MinedTopParam minedTopParam = new MinedTopParam();
            minedTopParam.setOrderByClause("id asc");
            minedTopParam.setStart(page.getStart());
            minedTopParam.setCount(pageSize);
            List<MinedTop> list = minedTopBusiness.selectMinedTopList(minedTopParam);
            page.setList(list);
            result = RpcClientResult.getSuccess();
            result.setData(page);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/txhistory")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult txhistory() {
        RpcClientResult result;
        try {
            TxHistoryParam txHistoryParam = new TxHistoryParam();
            txHistoryParam.setOrderByClause("id desc");
            txHistoryParam.setStart(0);
            txHistoryParam.setCount(14);
            List<TxHistory> list = txHistoryBusiness.selectTxHistoryList(txHistoryParam);
            result = RpcClientResult.getSuccess();
            result.setData(list);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

}
