package io.nuls.api.server.business;

import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Transaction;
import io.nuls.api.exception.NulsException;
import io.nuls.api.server.dao.mapper.BlockHeaderMapper;
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
    @Autowired
    private BlockHeaderMapper blockHeaderMapper;
    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;
    /**
     * 同步最新块数据
     *
     * @param block
     */
    @Transactional
    public void syncData(Block block) throws NulsException {
        blockHeaderMapper.insert(block.getHeader());

        for (int i = 0; i < block.getTxList().size(); i++) {
            Transaction tx = block.getTxList().get(i);
            utxoBusiness.updateByFrom(tx);
            utxoBusiness.saveTo(tx);

            transactionBusiness.insert(tx);
        }
    }

    /**
     * 回滚当前本地最新块
     */
    @Transactional
    public void rollback(BlockHeader block) throws NulsException {

    }

}
