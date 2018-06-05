package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.BlockHeader;
import io.nuls.api.server.dao.util.Searchable;

@MyBatisMapper
public interface BlockHeaderMapper extends BaseMapper<BlockHeader, String>{


    BlockHeader getBestBlock();

    /**
     * 时间段内交易笔数统计
     * @param searchable
     * @return
     */
    Integer getBlockSumTxcount(Searchable searchable);
}