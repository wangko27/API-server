package io.nuls.api.server.task;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.WebwalletTransaction;
import io.nuls.api.server.business.WebwalletTransactionBusiness;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: zsj
 * Date:  2018/8/16 0016
 */
@Component
public class WebwalletTransactionsyncTask {

    @Autowired
    private SyncDataHandler syncDataHandler;

    @Autowired
    private WebwalletTransactionBusiness webwalletTransactionBusiness;

    /**
     * 每隔十分钟去未确认交易表中查询所有未确认的交易，去链上验证，验证通过就重发，验证不通过就删除
     */
    public void execute(){
        System.out.println("----------------------每隔十分钟，清理未确认交易------------------------------");
        List<WebwalletTransaction> list = webwalletTransactionBusiness.getListByTime(EntityConstant.WEBWALLET_STATUS_NOTCONFIRM, TimeService.currentTimeMillis()-600000);
        Map<String, String> params = new HashMap<>();
        RpcClientResult result = null;
        for(WebwalletTransaction webwalletTransaction:list){
            params.put("txHex", webwalletTransaction.getSignData());
            result = syncDataHandler.valiTransaction(params);
            if(result.isSuccess()){
                syncDataHandler.broadcast(params);
            }else{
                webwalletTransactionBusiness.deleteByKey(webwalletTransaction.getHash());
            }
        }
    }
}
