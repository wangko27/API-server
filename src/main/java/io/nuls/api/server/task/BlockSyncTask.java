package io.nuls.api.server.task;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.exception.NulsException;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.SyncDataBusiness;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.text.SimpleDateFormat;

@Component
public class BlockSyncTask {

    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private SyncDataBusiness syncDataBusiness;
    @Autowired
    private SyncDataHandler syncDataHandler;

    /**
     * 同步区块
     */
    public void execute() {
        //查询本地已保存的最新块
        BlockHeader localBest = null;
        long bestHeight = -1;
        while (true) {
            try {
                localBest = blockBusiness.getNewest();
                if (localBest != null) {
                    bestHeight = localBest.getHeight();
                }

                RpcClientResult<BlockHeader> result = syncDataHandler.getBlockHeader(bestHeight + 1);
                if (result.isFailed()) {
                    if (result.getCode().equals(ErrorCode.DATA_NOT_FOUND.getCode())) {
                        //查看最新区块和本地区块是否一致，不一致说明需要回滚
                        result = syncDataHandler.getNewest();
                        if (result.isSuccess()) {
                            BlockHeader newest = result.getData();
                            if (!localBest.getHash().equals(newest.getHash()) && newest.getHeight() <= localBest.getHeight()) {
                                syncDataBusiness.rollback(localBest);
                            } else {
                                //没有新区块，跳出循环，等待下次轮询
                                return;
                            }
                        } else {
                            Log.error("----------------------sync block fail:" + result.getMsg());
                            return;
                        }
                    } else {
                        Log.error("----------------------sync block fail:" + result.getMsg());
                        return;
                    }
                } else {
                    BlockHeader newest = result.getData();
                    if (checkBlockContinuity(localBest, newest)) {

                        RpcClientResult<Block> blockResult = syncDataHandler.getBlock(newest);

                        if (blockResult.isFailed()) {
                            throw new NulsException(blockResult.getCode(), blockResult.getMsg());
                        }
                        syncDataBusiness.syncData(blockResult.getData());
                    } else {
                        syncDataBusiness.rollback(localBest);
                    }
                }
            } catch (NulsException ne) {
                Log.error("------------ sync block exception , block height is:" + bestHeight);
                Log.error(ne.getMsg(), ne);
                if (localBest != null) {
                    try {
                        syncDataBusiness.rollback(localBest);
                    } catch (Exception e) {
                        Log.error(e);
                    }
                }
            } catch (Exception e) {
                if (e instanceof ConnectException) {
                    return;
                }
                Log.error("------------ sync block exception , block height is:" + bestHeight);
                Log.error(e);
                if (localBest != null) {
                    try {
                        syncDataBusiness.rollback(localBest);
                    } catch (Exception ne) {
                        Log.error(ne);
                    }
                }
            }
        }
    }


    /**
     * 区块连续性检查
     *
     * @param localBest
     * @param newest
     * @return
     */
    private boolean checkBlockContinuity(BlockHeader localBest, BlockHeader newest) {
        if (localBest == null) {
            if (newest.getHeight() == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            if (newest.getHeight() == localBest.getHeight() + 1) {
                if (newest.getPreHash().equals(localBest.getHash())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
