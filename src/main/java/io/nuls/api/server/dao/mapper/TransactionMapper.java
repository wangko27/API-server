package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Transaction;

public interface TransactionMapper {

    int deleteByPrimaryKey(String hash);

    int insert(Transaction record);

    int insertSelective(Transaction record);

    Transaction selectByPrimaryKey(String hash);

    int updateByPrimaryKeySelective(Transaction record);

    int updateByPrimaryKeyWithBLOBs(Transaction record);

    int updateByPrimaryKey(Transaction record);
}