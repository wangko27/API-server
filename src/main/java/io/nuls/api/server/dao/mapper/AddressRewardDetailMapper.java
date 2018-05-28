package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.AddressRewardDetail;

public interface AddressRewardDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AddressRewardDetail record);

    int insertSelective(AddressRewardDetail record);

    AddressRewardDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AddressRewardDetail record);

    int updateByPrimaryKey(AddressRewardDetail record);
}