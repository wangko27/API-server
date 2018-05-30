package io.nuls.api.server.business;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.server.dao.mapper.BlockHeaderMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void saveBlock(BlockHeader blockHeader) {
        blockHeaderMapper.insert(blockHeader);
    }

    public List<BlockHeader> getBlockList(long beginHeight, long endHeight) {
        Searchable searchable = new Searchable();
        if(beginHeight >= 0){
            searchable.addCondition("height", SearchOperator.gte, beginHeight);
        }
        if(endHeight > 0){
            searchable.addCondition("height", SearchOperator.lte, endHeight);
        }
        return blockHeaderMapper.selectList(searchable);
    }
    public List<BlockHeader> getList() {
        Searchable searchable = new Searchable();
        return blockHeaderMapper.selectList(searchable);
    }


    public List<BlockHeader> getNewest() {
        //todo
        Searchable searchable = new Searchable();
        return blockHeaderMapper.selectList(searchable);
    }
    /**
     * 根据最新传入的区块信息，验证当前区块和前一区块的连续性
     *
     * @param blockHeader
     * @return
     */
    public boolean validatePreBlock(BlockHeader blockHeader) {
        BlockHeader preBlock = getBlockByHash(blockHeader.getPreHash());
        if (preBlock == null) {
            return false;
        }
        if (preBlock.getHeight() != blockHeader.getHeight() - 1) {
            return false;
        }
        return true;
    }

    @Transactional
    public int deleteBlock(String hash) {
        return blockHeaderMapper.deleteByPrimaryKey(hash);
    }

}
