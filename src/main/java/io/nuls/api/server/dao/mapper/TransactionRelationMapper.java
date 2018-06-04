package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.TransactionRelationKey;

@MyBatisMapper
public interface TransactionRelationMapper {

    int deleteByPrimaryKey(TransactionRelationKey key);

    int insert(TransactionRelationKey record);

    int insertSelective(TransactionRelationKey record);
}