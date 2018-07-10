package io.nuls.api.server;

import io.nuls.api.constant.Constant;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.*;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.NulsDigestData;
import io.nuls.api.model.Result;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.SyncDataBusiness;
import io.nuls.api.server.dao.mapper.leveldb.BlockHeaderLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.TransactionLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.leveldb.manager.LevelDBManager;
import io.nuls.api.server.leveldb.service.DBService;
import io.nuls.api.server.leveldb.service.impl.LevelDBServiceImpl;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.RestFulUtils;
import net.sf.ehcache.util.FindBugsSuppressWarnings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
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
    @Autowired
    private BlockHeaderLevelDbService headerLevelDbService;
    @Autowired
    private TransactionLevelDbService transactionLevelDbService;
    @Autowired
    private UtxoLevelDbService utxoLevelDbService;


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
