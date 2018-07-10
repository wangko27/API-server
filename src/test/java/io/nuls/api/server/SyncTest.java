package io.nuls.api.server;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.*;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.NulsDigestData;
import io.nuls.api.model.Result;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.SyncDataBusiness;
import io.nuls.api.server.leveldb.manager.LevelDBManager;
import io.nuls.api.server.leveldb.service.DBService;
import io.nuls.api.server.leveldb.service.impl.LevelDBServiceImpl;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

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
    private static DBService dbService;

    private static String areaName = "blockDB";

    @Before
    public void init() {
        RestFulUtils.getInstance().init("http://192.168.1.233:8001/api");
//        try {
//            LevelDBManager.init();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //utxo
//        dbService.createArea(Constant.UTXO_DB_NAME);
//        //transaction
//        dbService.createArea(Constant.TRANSACTION_DB_NAME);
//        //blockheader
//        dbService.createArea(Constant.BLOCKHEADER_DB_NAME);
    }

    @Test
    public void testGetBlock() {
        for (int i = 0; i <= 50000; i++) {
            RpcClientResult<BlockHeader> result = syncDataHandler.getBlockHeader(i);
            BlockHeader header = result.getData();
            Result result1 = dbService.putModel(areaName, header.getHash().getBytes(), header);

        }
    }


    @Test
    public void testGetBlockDBlevel() {
        BlockHeader header = dbService.getModel(areaName, "0020b88302681a0fb27e12aa9bc2e42d9c9310b5d9e5dc5a35d4af71c6382f10815a".getBytes(), BlockHeader.class);
        System.out.println(header.getHeight());
    }


    @Test
    public void testBlock() {
        BlockHeader block = blockBusiness.getByKey(5486L);

        try {
            System.out.println(new String(block.getExtend(), Constant.DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
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
        for (long i = 3325; i >= 0; i--) {
            BlockHeader blockHeader = blockBusiness.getByKey(i);
            try {
                syncDataBusiness.rollback(blockHeader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
