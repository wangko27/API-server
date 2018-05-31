package io.nuls.api.server.task;

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

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
