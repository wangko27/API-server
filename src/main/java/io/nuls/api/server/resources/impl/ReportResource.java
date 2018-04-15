package io.nuls.api.server.resources.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.counter.QueryCounter;
import io.nuls.api.entity.*;
import io.nuls.api.server.business.*;
import io.nuls.api.server.dto.Page;
import io.nuls.api.server.resources.QueryHelper;
import io.nuls.api.utils.RestFulUtils;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        try {
            Page<BalanceTop> page = new Page<>();
            page.setPageNumber(pageNumber);
            page.setPageSize(pageSize);
            page.setTotal(QueryCounter.getBalance(balanceTopBusiness));
            BalanceTopParam balanceTopParam = new BalanceTopParam();
            List<BalanceTop> list;
            // first page, default query way -> eg. limit 0, 20
            if(pageNumber == 1) {
                balanceTopParam.setOrderByClause("id asc");
                balanceTopParam.setStart(page.getStart());
                balanceTopParam.setCount(pageSize);
                list = balanceTopBusiness.selectBalanceTopList(balanceTopParam);
            } else {
                // page 2 or larger page, skipping id query way -> eg. where id > 20 limit 20
                balanceTopParam.setOrderByClause("id asc");
                BalanceTopParam.Criteria criteria = balanceTopParam.createCriteria();
                Long prePageMaxId = QueryHelper.HELPER.getCache(pageSize + "-balancelist-"+(pageNumber-1));
                if(prePageMaxId != null) {
                    balanceTopParam.setStart(pageSize);
                    criteria.andIdGreaterThan(prePageMaxId);
                } else {
                    balanceTopParam.setStart(page.getStart());
                    balanceTopParam.setCount(pageSize);
                }
                list = balanceTopBusiness.selectBalanceTopList(balanceTopParam);
            }
            if(list != null && list.size() > 0)
                QueryHelper.HELPER.putCache(pageSize + "-balancelist-"+pageNumber, list.get(list.size() - 1).getId());
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
        try {
            Page<MinedTop> page = new Page<>();
            page.setPageNumber(pageNumber);
            page.setPageSize(pageSize);
            page.setTotal(QueryCounter.getMined(minedTopBusiness));
            MinedTopParam minedTopParam = new MinedTopParam();
            List<MinedTop> list;

            // first page, default query way -> eg. limit 0, 20
            if(pageNumber == 1) {
                minedTopParam.setOrderByClause("id asc");
                minedTopParam.setStart(page.getStart());
                minedTopParam.setCount(pageSize);
                list = minedTopBusiness.selectMinedTopList(minedTopParam);
            } else {
                // page 2 or larger page query way -> eg. where id > 20 limit 20
                minedTopParam.setOrderByClause("id asc");
                MinedTopParam.Criteria criteria = minedTopParam.createCriteria();
                Long prePageMaxId = QueryHelper.HELPER.getCache(pageSize + "-minedlist-"+(pageNumber-1));
                if(prePageMaxId != null) {
                    minedTopParam.setStart(pageSize);
                    criteria.andIdGreaterThan(prePageMaxId);
                } else {
                    minedTopParam.setStart(page.getStart());
                    minedTopParam.setCount(pageSize);
                }
                list = minedTopBusiness.selectMinedTopList(minedTopParam);
            }
            if(list != null && list.size() > 0)
                QueryHelper.HELPER.putCache(pageSize + "-minedlist-"+pageNumber, list.get(list.size() - 1).getId());

            try {
                RpcClientResult status = RestFulUtils.getInstance().get("/consensus/agent/status", null);
                Map<String, String> statusMap = (Map<String, String>) status.getData();

                //Map<String, String> statusMap = statusList.stream().collect(Collectors.toMap(map -> map.get("address"), map -> map.get("consensusStatus")));

                list.stream().forEach(minedTop -> {
                    String _status = "0";
                    if(statusMap != null) {
                        _status = statusMap.get(minedTop.getAgentAddress());
                        if(_status == null)
                            _status = "0";
                    }
                    minedTop.setConsensusStatus(_status);
                });

            } catch (Exception e) {
                Log.warn("can not get consensus status list.", e);
                // skip
            }

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
