package io.nuls.api.server;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Transaction;
import io.nuls.api.model.NulsDigestData;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.SyncDataBusiness;
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

    @Before
    public void init() {
        RestFulUtils.getInstance().init("http://127.0.0.1:8001");
    }

    @Test
    public void testBlock() {
        BlockHeader block = blockBusiness.getBlockByHeight(0);

        try {
            System.out.println(new String(block.getExtend(), Constant.DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetBlock() {
        for (int i = 0; i < 6000; i++) {
            RpcClientResult<BlockHeader> result = syncDataHandler.getBlockHeader(i);
            BlockHeader header = result.getData();
            try {
                RpcClientResult<Block> blockResult = syncDataHandler.getBlock(header);
                syncDataBusiness.syncData(blockResult.getData());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Test
    public void testRollback() {
        for (int i = 1120; i >= 0; i--) {
            BlockHeader blockHeader = blockBusiness.getBlockByHeight(i);
            try {
                syncDataBusiness.rollback(blockHeader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
