package io.nuls.api.server.business;

import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.*;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void syncData(Block block) throws Exception {
        System.out.println("-------------------------------sync block---------" + block.getHeader().getHeight());
        long time1 = System.currentTimeMillis();
        try {
            blockBusiness.saveBlock(block.getHeader());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        long time2 = System.currentTimeMillis();
        if (time2 - time1 > 10) {
            System.out.println("---------------saveBlock:" + (time2 - time1) + ",hash:" + block.getHeader().getHash());
        }

        time1 = System.currentTimeMillis();
        for (int i = 0; i < block.getTxList().size(); i++) {
            Transaction tx = block.getTxList().get(i);
            tx.setTxIndex(i);
            utxoBusiness.updateByFrom(tx);

            utxoBusiness.saveTo(tx);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("scriptSign", tx.getScriptSign());
            dataMap.put("inputs", tx.getInputs());
            dataMap.put("outputs", tx.getOutputList());
            tx.setExtend(JSONUtils.obj2json(dataMap).getBytes());

            transactionBusiness.save(tx);
        }
        time2 = System.currentTimeMillis();
        if(time2 - time1 > 100) {
            System.out.println("------------------------transactionBusiness save:" + (time2 - time1) + ",count" + block.getTxList().size());
        }

        //所有数据保存成功后，更新utxo缓存
        time1 = System.currentTimeMillis();
        UtxoKey key = new UtxoKey();
        Utxo utxo;
        for (int i = 0; i < block.getTxList().size(); i++) {
            Transaction tx = block.getTxList().get(i);
            if (tx.getInputs() != null) {
                for (int j = 0; j < tx.getInputs().size(); j++) {
                    Input input = tx.getInputs().get(j);
                    key.setTxHash(input.getFromHash());
                    key.setTxIndex(input.getFromIndex());
                    utxo = utxoBusiness.getByKey(key);
                    UtxoContext.remove(utxo);
                }
            }
            if (tx.getOutputs() != null) {
                for (int j = 0; j < tx.getOutputs().size(); j++) {
                    UtxoContext.put(tx.getOutputs().get(j));
                }
            }
        }
        time2 = System.currentTimeMillis();
        if (time2 - time1 > 200) {
            System.out.println("---------------update UtxoContext:" + (time2 - time1));
        }
    }

    /**
     * 回滚当前本地最新块
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollback(BlockHeader header) throws Exception {
        Log.error("--------------roll back, block height: " + header.getHeight() + ",hash :" + header.getHash());
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

        //回滚缓存
        for (int i = txList.size() - 1; i >= 0; i--) {
            Transaction tx = txList.get(i);
            if (tx.getOutputs() != null) {
                for (int j = tx.getOutputs().size() - 1; j >= 0; j--) {
                    UtxoContext.remove(tx.getOutputs().get(j));
                }
            }
            UtxoKey utxoKey;
            Utxo utxo;
            if (tx.getInputs() != null) {
                for (int j = tx.getInputs().size() - 1; j >= 0; j--) {
                    Input input = tx.getInputs().get(j);
                    utxoKey = new UtxoKey(input.getFromHash(), input.getFromIndex());
                    utxo = utxoBusiness.getByKey(utxoKey);
                    if (utxo != null) {
                        UtxoContext.put(utxo);
                    }
                }
            }
        }
    }

}
