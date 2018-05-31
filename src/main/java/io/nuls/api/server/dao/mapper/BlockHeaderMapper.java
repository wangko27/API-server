package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.BlockHeader;

@MyBatisMapper
public interface BlockHeaderMapper extends BaseMapper<BlockHeader, String>{


    BlockHeader getBestBlock();
}