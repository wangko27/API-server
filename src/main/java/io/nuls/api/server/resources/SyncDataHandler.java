package io.nuls.api.server.resources;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.RestFulUtils;
import org.springframework.stereotype.Component;

/**
 * 同步底层数据的处理器
 */
@Component
public class SyncDataHandler {

    private RestFulUtils restFulUtils = RestFulUtils.getInstance();

    /**
     * 按照高度同步区块
     *
     * @param height
     * @return
     */
    public RpcClientResult getBlockHeader(long height) {
        RpcClientResult result = restFulUtils.get("/block/height/" + height, null);
        if (!result.isSuccess()) {
            return result;
        }
        try {
            String json = JSONUtils.obj2json(result.getData());
            BlockHeader blockHeader = JSONUtils.json2pojo(json, BlockHeader.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
