package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.server.dao.mapper.BlockHeaderMapper;
import io.nuls.api.server.dao.mapper.leveldb.BlockHeaderLevelDbService;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

/**
 * 区块头处理器，包括区块的验证，回滚，查询与存储等
 */
@Service
public class BlockBusiness implements BaseService<BlockHeader, Long> {

    @Autowired
    private BlockHeaderMapper blockHeaderMapper;
    @Autowired
    private AgentNodeBusiness agentNodeBusiness;
    @Autowired
    private AddressRewardDetailBusiness rewardDetailBusiness;

    private BlockHeaderLevelDbService blockHeaderLevelDbService = BlockHeaderLevelDbService.getInstance();

    public BlockHeader getBlockByHash(String hash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("hash", SearchOperator.eq, hash);
        BlockHeader blockHeader = blockHeaderMapper.selectBySearchable(searchable);
        if(null != blockHeader){
            return blockHeaderLevelDbService.select(blockHeader.getHeight()+"");
        }
        return null;
    }

    public BlockHeader getBlockByHeight(long height) {
        return blockHeaderMapper.selectByPrimaryKey(height);
    }

    /**
     * 查询某时间段内的交易笔数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    //todo 统计交易笔数
    public Integer getTxcountByTime(Long startTime, Long endTime) {
        /*if (startTime < 0 || endTime < 0) {
            return 0;
        }
        Searchable searchable = new Searchable();
        searchable.addCondition("create_time", SearchOperator.gte, startTime);
        searchable.addCondition("create_time", SearchOperator.lt, endTime);
        return blockHeaderMapper.getBlockSumTxcount(searchable);*/
        return 0;

    }

    /**
     * 查询24小时共识奖励
     *
     * @param startTime
     * @return
     */
    public Long getBlockSumRewardByTime(Long startTime) {
        /*if (startTime < 0) {
            return 0L;
        }
        Searchable searchable = new Searchable();
        searchable.addCondition("create_time", SearchOperator.gte, startTime-Constant.MILLISECONDS_TIME_DAY);
        searchable.addCondition("create_time", SearchOperator.lt, startTime);
        return blockHeaderMapper.getBlockSumReward(searchable);*/
        //todo 24小时共识奖励统计
        return 0L;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveBlock(BlockHeader blockHeader) {
        //存入数据库
        blockHeaderMapper.insert(blockHeader);
        //存入leveldb
        blockHeaderLevelDbService.insert(blockHeader);

        //修改agentnode
        AgentNode agentNode = agentNodeBusiness.getAgentByAddress(blockHeader.getConsensusAddress());
        if (agentNode != null) {
            agentNode.setLastRewardHeight(blockHeader.getHeight());
            agentNode.setTotalPackingCount(agentNode.getTotalPackingCount() + 1);
            agentNode.setTotalReward(agentNode.getTotalReward() + blockHeader.getReward());
            agentNodeBusiness.update(agentNode);
        }
    }

    //todo 查询，需要联合leveldb
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

    //todo 查询，需要联合leveldb
    /**
     * 获取块列表
     *
     * @param address    共识地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<BlockHeader> getList(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            if (StringUtils.validAddress(address)) {
                searchable.addCondition("consensus_address", SearchOperator.eq, address);
            } else {
                return null;
            }
        }
        PageHelper.orderBy("height desc");
        PageInfo<BlockHeader> page = new PageInfo<>(blockHeaderMapper.selectList(searchable));
        return page;
    }

    //todo 查询，需要联合leveldb
    /**
     * 获取块列表
     *
     * @param address    共识地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public List<BlockHeader> getListAll(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            if (StringUtils.validAddress(address)) {
                searchable.addCondition("consensus_address", SearchOperator.eq, address);
            } else {
                return null;
            }
        }
        PageHelper.orderBy("height desc");
        return blockHeaderMapper.selectList(searchable);
    }

    //todo 查询，需要联合leveldb
    public BlockHeader getNewest() {
        return blockHeaderMapper.getBestBlock();
    }

    //todo 查询，需要联合leveldb
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
        BlockHeader preBlock = getBlockByHeight(blockHeader.getHeight() - 1);
        if (preBlock == null) {
            return false;
        }
        if (preBlock.getHeight() != blockHeader.getHeight() - 1) {
            return false;
        }
        return true;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteBlock(Long blockHeight) {
        blockHeaderLevelDbService.delete((blockHeight+""));
        return blockHeaderMapper.deleteByPrimaryKey(blockHeight);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(BlockHeader blockHeader) {
        blockHeaderLevelDbService.insert(blockHeader);
        return blockHeaderMapper.insert(blockHeader);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(BlockHeader blockHeader) {
        blockHeaderLevelDbService.insert(blockHeader);
        return blockHeaderMapper.updateByPrimaryKey(blockHeader);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(Long blockHeight) {
        //todo 查询，需要联合leveldb
        BlockHeader header = blockHeaderMapper.selectByPrimaryKey(blockHeight);
        AgentNode agentNode = agentNodeBusiness.getAgentByAddress(header.getConsensusAddress());
        if (agentNode != null) {
            Long height = rewardDetailBusiness.getLastRewardHeight(agentNode.getRewardAddress());
            agentNode.setLastRewardHeight(height);
            agentNode.setTotalPackingCount(agentNode.getTotalPackingCount() - 1);
            agentNode.setTotalReward(agentNode.getTotalReward() - header.getReward());
            agentNodeBusiness.update(agentNode);

        }
        blockHeaderLevelDbService.delete((blockHeight+""));
        return blockHeaderMapper.deleteByPrimaryKey(blockHeight);
    }

    @Override
    public BlockHeader getByKey(Long blockHeight) {
        //return blockHeaderMapper.selectByPrimaryKey(blockHeight);
        return blockHeaderLevelDbService.select((blockHeight+""));
    }

    /**
     * 统计出块历史
     */
    public void initHistory() {
        String[] historyList = new String[14];
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long time = cal.getTime().getTime();
        for (int i = 13; i >= 0; i--) {
            Integer count = getTxcountByTime(time - Constant.MILLISECONDS_TIME_DAY, time);
            time = time - Constant.MILLISECONDS_TIME_DAY;
            String values = null == count ? time+"-0":time + "-" + count;
            historyList[i] = values;
        }
        HistoryContext.reset(historyList);
    }
}
