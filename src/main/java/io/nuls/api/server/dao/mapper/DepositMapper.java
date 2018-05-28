package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Deposit;

public interface DepositMapper {
    int deleteByPrimaryKey(String txHash);

    int insert(Deposit record);

    int insertSelective(Deposit record);

    Deposit selectByPrimaryKey(String txHash);

    int updateByPrimaryKeySelective(Deposit record);

    int updateByPrimaryKey(Deposit record);
}