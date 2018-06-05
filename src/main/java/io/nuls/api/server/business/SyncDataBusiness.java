package io.nuls.api.server.business;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Transaction;
import io.nuls.api.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncDataBusiness {

    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private AliasBusiness aliasBusiness;
    @Autowired
    private PunishLogBusiness punishLogBusiness;
    @Autowired
    private AddressRewardDetailBusiness detailBusiness;

    /**
     * 同步最新块数据
     *
     * @param block
     */
    @Transactional
    public void syncData(Block block) throws Exception {
        blockBusiness.saveBlock(block.getHeader());
        for (int i = 0; i < block.getTxList().size(); i++) {
            Transaction tx = block.getTxList().get(i);
            tx.setTxIndex(i);
            if (tx.getType() == EntityConstant.TX_TYPE_STOP_AGENT) {
                System.out.println(tx.getType());
            }

            utxoBusiness.updateByFrom(tx);
            utxoBusiness.saveTo(tx);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("scriptSign", tx.getScriptSign());
            dataMap.put("inputs", tx.getInputs());
            dataMap.put("outputs", tx.getOutputList());
            tx.setExtend(JSONUtils.obj2json(dataMap).getBytes());

            transactionBusiness.save(tx);
        }

        System.out.println("----------------save block:" + block.getHeader().getHeight());
    }

    /**
     * 回滚当前本地最新块
     */
    @Transactional
    public void rollback(BlockHeader header) throws Exception {
        List<Transaction> txList = transactionBusiness.getList(header.getHeight());
        for (int i = txList.size() - 1; i >= 0; i--) {
            Transaction tx = txList.get(i);
            transactionBusiness.rollback(tx);
        }
        //回滚别名
        aliasBusiness.deleteByHeight(header.getHeight());
        //回滚惩罚记录
        punishLogBusiness.deleteByHeight(header.getHeight());
        //回滚奖励
        detailBusiness.deleteByHeight(header.getHeight());
        //回滚块
        blockBusiness.deleteByKey(header.getHash());
        System.out.println("----------------save block:" + header.getHeight());
    }

}
