package io.nuls.api.server.business;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.server.dao.mapper.BlockHeaderMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.plugin2.os.windows.SECURITY_ATTRIBUTES;

import java.util.List;

/**
 * 区块头处理器，包括区块的验证，回滚，查询与存储等
 */
@Service
public class BlockBusiness {

    @Autowired
    private BlockHeaderMapper blockHeaderMapper;

    public BlockHeader getBlockByHash(String hash) {
        return blockHeaderMapper.selectByPrimaryKey(hash);
    }

    public BlockHeader getBlockByHeight(long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("height", SearchOperator.eq, height);
        return blockHeaderMapper.selectBySearchable(searchable);
    }

    public void saveBlock(BlockHeader blockHeader) {
        blockHeaderMapper.insert(blockHeader);
    }

    public List<BlockHeader> getBlockList(long beginHeight, long endHeight) {
        Searchable searchable = new Searchable();
        searchable.addCondition("height", SearchOperator.gte, beginHeight);
        searchable.addCondition("height", SearchOperator.lte, endHeight);
        return blockHeaderMapper.selectList(searchable);
    }

}
