package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.Block;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.dao.mapper.BlockHeaderMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.BlockDto;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区块头处理器，包括区块的验证，回滚，查询与存储等
 */
@Service
public class BlockBusiness implements BaseService<BlockHeader, String> {

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

    /**
     * 查询某时间段内的交易笔数
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer getTxcountByTime(Long startTime,Long endTime){
        if(startTime < 0 || endTime < 0){
            return 0;
        }
        Searchable searchable = new Searchable();
        searchable.addCondition("create_time", SearchOperator.gte, startTime);
        searchable.addCondition("create_time", SearchOperator.lt, endTime);
        return  blockHeaderMapper.getBlockSumTxcount(searchable);
    }

    public List<BlockHeader> getBlockList(long beginHeight, long endHeight) {
        Searchable searchable = new Searchable();
        if (beginHeight >= 0) {
            searchable.addCondition("height", SearchOperator.gte, beginHeight);
        }
        if (endHeight > 0) {
            searchable.addCondition("height", SearchOperator.lte, endHeight);
        }
        return blockHeaderMapper.selectList(searchable);
    }

    /**
     * 获取块列表
     * @param address 共识地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<BlockHeader> getList(String address,int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(address)){
            if(StringUtils.validAddress(address)){
                searchable.addCondition("consensus_address", SearchOperator.eq, address);
            }else{
                return null;
            }
        }
        PageInfo<BlockHeader> page = new PageInfo<>(blockHeaderMapper.selectList(searchable));
        return page;
    }

    public BlockHeader getNewest() {
        return blockHeaderMapper.getBestBlock();
    }

    public PageInfo<BlockHeader> getBlockPage(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        List<BlockHeader> list = blockHeaderMapper.selectList(searchable);
        PageInfo<BlockHeader> page = new PageInfo<>(list);
        return page;
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

    @Transactional
    @Override
    public int save(BlockHeader blockHeader) {
        return blockHeaderMapper.insert(blockHeader);
    }

    @Transactional
    @Override
    public int update(BlockHeader blockHeader) {
        return blockHeaderMapper.updateByPrimaryKey(blockHeader);
    }

    @Transactional
    @Override
    public int deleteByKey(String s) {
        return blockHeaderMapper.deleteByPrimaryKey(s);
    }

    @Override
    public BlockHeader getByKey(String s) {
        return blockHeaderMapper.selectByPrimaryKey(s);
    }
}
