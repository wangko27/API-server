package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.BlockHeader;

public interface BlockHeaderMapper {
    int deleteByPrimaryKey(String hash);

    int insert(BlockHeader record);

    int insertSelective(BlockHeader record);

    BlockHeader selectByPrimaryKey(String hash);

    int updateByPrimaryKeySelective(BlockHeader record);

    int updateByPrimaryKeyWithBLOBs(BlockHeader record);

    int updateByPrimaryKey(BlockHeader record);
}