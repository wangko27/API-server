package io.nuls.api.server.task;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.resources.SyncDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class BlockSyncTask {

    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private SyncDataHandler syncDataHandler;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 同步区块
     */
    public void execute() {
        //查询本地已保存的最新块
        BlockHeader best = blockBusiness.getNewest();
        long bestHeight = -1;
        if (best != null) {
            bestHeight = best.getHeight();
        }

        //获取网络的下一块
        bestHeight += 1;
        RpcClientResult<BlockHeader> result = syncDataHandler.getBlockHeader(bestHeight);
        if(result.isFaild()) {
           // if(result.)
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
