package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.BlockHeader;
import io.nuls.api.server.dao.util.Searchable;

@MyBatisMapper
public interface BlockHeaderMapper extends BaseMapper<BlockHeader, Long>{
    /**
     * 获取最新区块
     * @return
     */
    //todo 最新区块，直接缓存到ehcache中，新增的时候更新，回滚的时候去数据库查询最新一块
    BlockHeader getBestBlock();

    //todo 某个时间段内的交易笔数
    Integer getBlockSumTxcount(Searchable searchable);

    //todo 统计奖励总和
    Long getBlockSumReward(Searchable searchable);
}