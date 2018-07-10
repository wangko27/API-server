package io.nuls.api.server.dao.mapper.leveldb;

import io.nuls.api.constant.Constant;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.server.leveldb.service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/8 0008
 */
@Service
public class BlockHeaderLevelDbService {
    @Autowired
    private DBService dbService;

    public void insertList(List<BlockHeader> list) {
        BatchOperation batch = dbService.createWriteBatch(Constant.BLOCKHEADER_DB_NAME);
        for (BlockHeader block : list) {
            batch.putModel((block.getHeight() + "").getBytes(), block);
        }
        batch.executeBatch();
    }

    public int insert(BlockHeader blockHeader) {
        Result<BlockHeader> result = dbService.putModel(Constant.BLOCKHEADER_DB_NAME, blockHeader.getHash().getBytes(), blockHeader);
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public int delete(String key) {
        Result result = dbService.delete(Constant.BLOCKHEADER_DB_NAME, key.getBytes());
        if (result.isSuccess()) {
            return 1;
        }
        return 0;
    }

    public BlockHeader select(String key) {
        return dbService.getModel(Constant.BLOCKHEADER_DB_NAME, key.getBytes(), BlockHeader.class);
    }

    //这里会查询出leveldb里面全部的数据，谨慎使用
    public List<BlockHeader> getList() {
        return dbService.values(Constant.BLOCKHEADER_DB_NAME, BlockHeader.class);
    }
}
