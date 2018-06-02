package io.nuls.api.server;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.RestFulUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ApplicationContext.xml")
public class SyncTest {

    @Autowired
    private SyncDataHandler syncDataHandler;


    @Before
    public void init() {
        RestFulUtils.getInstance().init("http://127.0.0.1:8001");
    }

    @Test
    public void testGetBlock() {
        RpcClientResult<BlockHeader> result = syncDataHandler.getBlockHeader(1);

        System.out.println(result.getData().getPreHash());
    }
}
