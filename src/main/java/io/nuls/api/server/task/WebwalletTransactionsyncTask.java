package io.nuls.api.server.task;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Utxo;
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
 * Author: moon
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
                /*验证失败，需要删除，删除前也需要验证，找到这个地址的所有未确认交易，
                如果有交易的input使用了这个地址的output，那么相应的交易也需要删除
                */
                if(null != webwalletTransaction.getOutputs()){
                    StringBuilder splitStr = new StringBuilder();
                    List<Utxo> outputList = webwalletTransaction.getOutputs();
                    for(Utxo utxo:outputList){
                        splitStr.append(utxo.getTxHash()).append(",");
                    }
                    List<WebwalletTransaction> addrWebTransList = webwalletTransactionBusiness.getAll(webwalletTransaction.getAddress(),EntityConstant.WEBWALLET_STATUS_NOTCONFIRM,0);
                    for(WebwalletTransaction addrWeb:addrWebTransList){
                        for(Input attrInput : addrWeb.getInputs()){
                            if(attrInput.getFromHash().indexOf(splitStr.toString()) > 0){
                                webwalletTransactionBusiness.deleteByKey(addrWeb.getHash());
                                break;
                            }
                        }
                    }
                }
                webwalletTransactionBusiness.deleteByKey(webwalletTransaction.getHash());
            }
        }
    }
}
