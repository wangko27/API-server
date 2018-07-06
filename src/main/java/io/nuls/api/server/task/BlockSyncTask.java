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
        BlockHeader localBest = null;
        long bestHeight = -1;
        while (true) {
            //查询本地已保存的最新块
            localBest = blockBusiness.getNewest();
            if (localBest != null) {
                bestHeight = localBest.getHeight();
            }

            RpcClientResult<BlockHeader> result = null;
            try {
                result = syncDataHandler.getBlockHeader(bestHeight + 1);
            } catch (Exception e) {
                Log.error("--------获取下一区块头信息异常:" + e);
                return;
            }
            //失败处理
            if (result.isFailed()) {
                if (!result.getCode().equals(ErrorCode.DATA_NOT_FOUND.getCode())) {
                    //如果错误信息不是未找到最新块的话 就说明查询区块报错
                    Log.error("-------获取下一区块头信息失败:" + result.getCode() + "-" + result.getMsg());
                } else {
                    //错误信息是未找到时，就判断最新块是否一致，不一致说明需要回滚
                    try {
                        result = syncDataHandler.getNewest();
                    } catch (NulsException e) {
                        Log.error("-------查询最新区块头信息异常:" + e);
                        return;
                    }
                    if (result.isFailed()) {
                        Log.error("-------查询最新区块头信息失败:" + result.getCode() + "-" + result.getMsg());
                    }

                    BlockHeader newest = result.getData();
                    if (!localBest.getHash().equals(newest.getHash()) && newest.getHeight() <= localBest.getHeight()) {
                        try {
                            syncDataBusiness.rollback(localBest);
                        } catch (Exception e) {
                            Log.error("-------回滚本地最新区块失败:" + localBest.getHash());
                            Log.error(e);
                            return;
                        }
                    } else {
                        //没有新区块，跳出循环，等待下次轮询
                        return;
                    }
                }
            } else {
                //同步区块
                BlockHeader newest = result.getData();
                //区块连续性验证
                if (checkBlockContinuity(localBest, newest)) {
                    try {
                        RpcClientResult<Block> blockResult = syncDataHandler.getBlock(newest);
                        syncDataBusiness.syncData(blockResult.getData());
                    } catch (Exception e) {
                        Log.error("-------同步最新区块失败:" + newest.getHash());
                        Log.error(e);
                        return;
                    }

                } else {
                    try {
                        syncDataBusiness.rollback(localBest);
                    } catch (Exception e) {
                        Log.error("-------回滚本地最新区块失败:" + localBest.getHash());
                        Log.error(e);
                        return;
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
