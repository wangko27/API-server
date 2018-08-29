package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.BlockHeader;
import io.nuls.api.server.dao.util.Searchable;

@MyBatisMapper
public interface BlockHeaderMapper extends BaseMapper<BlockHeader, Long>{
    /**
     * 获取最新区块
     * @return
     */
    BlockHeader getBestBlock();
}