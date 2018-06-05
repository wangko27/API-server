package io.nuls.api.server.task;

import io.nuls.api.context.BalanceListContext;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.dto.UtxoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * Author: zsj
 * Date:  2018/6/5 0005
 */
@Component
public class InitUtxo {

    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private BlockBusiness blockBusiness;


    public void execute(){
        /*加载utxo*/
        List<Utxo> utxoList = utxoBusiness.getList(null,2);
        for(Utxo utxo:utxoList){
            UtxoContext.put(utxo);
        }
        /*加载持币账户排行榜*/
        List<UtxoDto> blockDtoList = utxoBusiness.getBlockSumTxamount();
        BalanceListContext.reset(blockDtoList);
        /*加载出块账户排行榜*/
    }
}
