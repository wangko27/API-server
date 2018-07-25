package io.nuls.api.server;

import io.nuls.api.entity.*;
import io.nuls.api.exception.NulsException;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.SyncDataBusiness;
import io.nuls.api.server.dao.mapper.leveldb.BlockHeaderLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.TransactionLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ApplicationContext.xml")
public class SyncTest {

    @Autowired
    private SyncDataHandler syncDataHandler;
    @Autowired
    private SyncDataBusiness syncDataBusiness;
    @Autowired
    private BlockBusiness blockBusiness;
    private BlockHeaderLevelDbService headerLevelDbService = BlockHeaderLevelDbService.getInstance();
    private TransactionLevelDbService transactionLevelDbService = TransactionLevelDbService.getInstance();
    private UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();

    @Test
    public void testBlockSync() {
        RestFulUtils.getInstance().init("http://192.168.1.109:8001/api");
        BlockHeader header = new BlockHeader();
        header.setHash("002082492ad4b7d56fdfd4f78b7a2f34868d4fc3064bd3d33f99fa9bfbae6d348505");
        try {
            RpcClientResult<Block> rpcClientResult = syncDataHandler.getBlock(header);
            Block block = rpcClientResult.getData();
            List<Transaction> txList = block.getTxList();
            System.out.println(txList.size());
        } catch (NulsException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSyncData() throws InterruptedException {
        while (true) {
            Thread.sleep(100000);
        }
    }

    @Test
    public void testNewest() {
        try {
            RpcClientResult<BlockHeader> result = syncDataHandler.getNewest();
            System.out.println(result.isSuccess());
        } catch (NulsException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGetUtxo() {
        String address = "6HgaqsowQbVM8AXbHbssSAAHddeypwcc";
        int limit = 0;
        RpcClientResult<Utxo> result = syncDataHandler.getUtxo(address, limit);

    }

    @Test
    public void testRollback() {
        for (long i = 6616; i >= 0; i--) {
            BlockHeader blockHeader = blockBusiness.getByKey(i);
            try {
                syncDataBusiness.rollback(blockHeader);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        List<BlockHeader> headers = headerLevelDbService.getList();
        System.out.println(headers == null);

        List<Transaction> transactions = transactionLevelDbService.getList();
        System.out.println(transactions == null);

        List<Utxo> utxos = utxoLevelDbService.getList();
        System.out.println(utxos == null);
    }
}
