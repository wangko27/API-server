package io.nuls.api.server.resources;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.entity.Block;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.entity.Utxo;
import io.nuls.api.exception.NulsException;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.RpcTransferUtil;
import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
    public RpcClientResult<BlockHeader> getBlockHeader(long height) {
        RpcClientResult result = restFulUtils.get("/block/header/height/" + height, null);
        if (result.isFailed()) {
            return result;
        }
        try {
            BlockHeader blockHeader = RpcTransferUtil.toBlockHeader((Map<String, Object>) result.getData());
            result.setData(blockHeader);
        } catch (Exception e) {
            Log.error(e);
            result = RpcClientResult.getFailed(KernelErrorCode.DATA_PARSE_ERROR);
        }
        return result;
    }

    public RpcClientResult<Block> getBlock(BlockHeader header) throws NulsException {
        Map<String, String> param = new HashMap<>();
        param.put("hash", header.getHash());
        RpcClientResult result = restFulUtils.get("/block/bytes", param);
        if (result.isFailed()) {
            return result;
        }
        Map<String, Object> resultMap = (Map<String, Object>) result.getData();
        String txHex = (String) resultMap.get("value");
        try {
            Block block = RpcTransferUtil.toBlock(txHex, header);
            result.setData(block);
        } catch (Exception e) {
            throw new NulsException(KernelErrorCode.DATA_PARSE_ERROR, e);
        }
        return result;
    }

    public RpcClientResult<BlockHeader> getNewest() throws NulsException {
        RpcClientResult result = restFulUtils.get("/block/newest", null);
        if (result.isFailed()) {
            return result;
        }
        try {
            BlockHeader blockHeader = RpcTransferUtil.toBlockHeader((Map<String, Object>) result.getData());
            result.setData(blockHeader);
        } catch (Exception e) {
            Log.error(e);
            result = RpcClientResult.getFailed(KernelErrorCode.DATA_PARSE_ERROR);
        }
        return result;
    }

    public RpcClientResult getTx(String hash) throws NulsException {
        RpcClientResult result = restFulUtils.get("/api/accountledger/tx/"+hash, null);
        if (result.isFailed()) {
            return result;
        }
        return null;
    }

    public RpcClientResult broadcast(Map<String, String> params){
        RpcClientResult result = restFulUtils.post("/accountledger/transaction/broadcast", params);
        return result;
    }

    public RpcClientResult valiTransaction(Map<String, String> params){
        RpcClientResult result = restFulUtils.post("/accountledger/transaction/valiTransaction", params);
        return result;
    }


    public RpcClientResult<Utxo> getUtxo(String address, int limit) {
        RpcClientResult result = restFulUtils.get("/utxo/limit/" + address + "/" + limit, null);
        return result;
    }
}
