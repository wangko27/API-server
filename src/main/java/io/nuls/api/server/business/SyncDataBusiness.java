package io.nuls.api.server.business;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Transaction;
import io.nuls.api.exception.NulsException;
import io.nuls.api.server.resources.SyncDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SyncDataBusiness {

    @Autowired
    private SyncDataHandler syncDataHandler;

    /**
     * 同步最新块数据
     *
     * @param block
     */
    @Transactional
    public void syncData(BlockHeader block) throws NulsException {
        //同步区块交易
        List<Transaction> txList = syncTransaction(block);
        for(Transaction tx : txList) {
        }
    }

    /**
     * 回滚当前本地最新块
     */
    @Transactional
    public void rollback(BlockHeader block) throws NulsException{

    }


    private List<Transaction> syncTransaction(BlockHeader block) throws NulsException {
        List<Transaction> txList = new ArrayList<>();
        RpcClientResult<Transaction> result;
        for (String txHash : block.getTxList()) {
            result = syncDataHandler.getTransaction(txHash);
            if (result.isFaild()) {
                throw new NulsException(result.getCode(), result.getMsg());
            }
            txList.add(result.getData());
        }
        return txList;
    }
}
