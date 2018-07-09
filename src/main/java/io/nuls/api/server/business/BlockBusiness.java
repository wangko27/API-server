package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.context.HistoryContext;
import io.nuls.api.context.IndexContext;
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
        return blockHeaderLevelDbService.select(hash);
    }

    public BlockHeader getBlockByHeight(long height) {
        BlockHeader blockHeader = blockHeaderMapper.selectByPrimaryKey(height);
        if(null != blockHeader){
            return blockHeaderLevelDbService.select(blockHeader.getHash());
        }
        return null;
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

    /**
     * 获取某个时间段内的区块
     * @param startTime
     * @return
     */
    public List<BlockHeader> getBlockByTime(Long startTime, Long endTime){
        Searchable searchable = new Searchable();
        searchable.addCondition("create_time", SearchOperator.gte, startTime);
        searchable.addCondition("create_time", SearchOperator.lt, endTime);
        List<BlockHeader> list = blockHeaderMapper.selectList(searchable);
        setDataWithLeveldb(list);
        return list;
    }

    /**
     * 查询列表
     * @param beginHeight
     * @param endHeight
     * @return
     */
    public List<BlockHeader> getBlockList(long beginHeight, long endHeight) {
        Searchable searchable = new Searchable();
        if (beginHeight >= 0) {
            if (beginHeight == 0) {
                beginHeight = -1;
            }
            searchable.addCondition("height", SearchOperator.gte, beginHeight);
        }
        if (endHeight > 0) {
            searchable.addCondition("height", SearchOperator.lte, endHeight);
        }
        List<BlockHeader> list = blockHeaderMapper.selectList(searchable);
        setDataWithLeveldb(list);
        return list;
    }

    /**
     * 获取块列表
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
        List<BlockHeader> blockHeaderList = blockHeaderMapper.selectList(searchable);
        setDataWithLeveldb(blockHeaderList);
        PageInfo<BlockHeader> page = new PageInfo<>(blockHeaderList);
        return page;
    }

    /**
     * 获取最新区块
     * @return
     */
    public BlockHeader getNewest() {
        //先从缓存加载，如果没有，再从数据库加载
        BlockHeader blockHeader = IndexContext.getBestNewBlock();
        if(null == blockHeader){
            blockHeader = blockHeaderMapper.getBestBlock();
            if(null != blockHeader){
                blockHeader = blockHeaderLevelDbService.select(blockHeader.getHash());
            }
        }
        return blockHeader;
    }

    /**
     * 根据最新传入的区块信息，验证当前区块和前一区块的连续性
     *
     * @param blockHeader
     * @return
     */
//    public boolean validatePreBlock(BlockHeader blockHeader) {
//        BlockHeader preBlock = getBlockByHeight(blockHeader.getHeight() - 1);
//        if (preBlock == null) {
//            return false;
//        }
//        if (preBlock.getHeight() != blockHeader.getHeight() - 1) {
//            return false;
//        }
//        return true;
//    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(BlockHeader blockHeader) {

        //修改agentnode
        AgentNode agentNode = agentNodeBusiness.getAgentByAddress(blockHeader.getConsensusAddress());
        if (agentNode != null) {
            agentNode.setLastRewardHeight(blockHeader.getHeight());
            agentNode.setTotalPackingCount(agentNode.getTotalPackingCount() + 1);
            agentNode.setTotalReward(agentNode.getTotalReward() + blockHeader.getReward());
            agentNodeBusiness.update(agentNode);
        }
        //存入数据库
        if (blockHeader.getHeight() == 0) {
            blockHeader.setHeight(-1L);
        }
        return blockHeaderMapper.insert(blockHeader);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(BlockHeader blockHeader) {
        blockHeaderLevelDbService.insert(blockHeader);
        return blockHeaderMapper.updateByPrimaryKey(blockHeader);
    }

    /**
     * 根据高度删除
     *
     * @param blockHeight
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(Long blockHeight) {
        if (blockHeight == 0) {
            blockHeight = -1L;
        }
        BlockHeader header = blockHeaderMapper.selectByPrimaryKey(blockHeight);
        if (null != header) {
            header = blockHeaderLevelDbService.select(header.getHash());
            AgentNode agentNode = agentNodeBusiness.getAgentByAddress(header.getConsensusAddress());
            if (agentNode != null) {
                Long height = rewardDetailBusiness.getLastRewardHeight(agentNode.getRewardAddress());
                agentNode.setLastRewardHeight(height);
                agentNode.setTotalPackingCount(agentNode.getTotalPackingCount() - 1);
                agentNode.setTotalReward(agentNode.getTotalReward() - header.getReward());
                agentNodeBusiness.update(agentNode);
            }
            blockHeaderLevelDbService.delete(header.getHash());
            return blockHeaderMapper.deleteByPrimaryKey(blockHeight);
        }
        return 0;
    }

    /**
     * 根据hash删除
     *
     * @param hash
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(String hash) {
        BlockHeader header = blockHeaderLevelDbService.select(hash);
        if (null != header) {
            AgentNode agentNode = agentNodeBusiness.getAgentByAddress(header.getConsensusAddress());
            if (agentNode != null) {
                Long height = rewardDetailBusiness.getLastRewardHeight(agentNode.getRewardAddress());
                agentNode.setLastRewardHeight(height);
                agentNode.setTotalPackingCount(agentNode.getTotalPackingCount() - 1);
                agentNode.setTotalReward(agentNode.getTotalReward() - header.getReward());
                agentNodeBusiness.update(agentNode);

            }
            blockHeaderLevelDbService.delete(header.getHash());
            if (header.getHeight() == 0) {
                header.setHeight(-1L);
            }
            return blockHeaderMapper.deleteByPrimaryKey(header.getHeight());
        }
        return 0;
    }

    /**
     * 根据高度获取
     *
     * @param height
     * @return
     */
    @Override
    public BlockHeader getByKey(Long height) {
        if (height == 0) {
            height = -1L;
        }
        BlockHeader blockHeader = blockHeaderMapper.selectByPrimaryKey(height);
        if (null != blockHeader) {
            return blockHeaderLevelDbService.select(blockHeader.getHash());
        }
        return null;
    }

    /**
     * 根据hash获取
     *
     * @param hash
     * @return
     */
    public BlockHeader getByKey(String hash) {
        return blockHeaderLevelDbService.select(hash);
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

    /**
     * 根据list，重置block数据
     * @param list
     */
    public void setDataWithLeveldb(List<BlockHeader> list){
        for(BlockHeader block:list){
            block = blockHeaderLevelDbService.select(block.getHash());
        }

    }
}
