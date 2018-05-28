package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Balance;

public interface BalanceMapper {
    int deleteByPrimaryKey(String address);

    int insert(Balance record);

    int insertSelective(Balance record);

    Balance selectByPrimaryKey(String address);

    int updateByPrimaryKeySelective(Balance record);

    int updateByPrimaryKey(Balance record);
}