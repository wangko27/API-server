package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractAddressInfo;

@MyBatisMapper
public interface ContractCallInfoMapper extends BaseMapper<ContractAddressInfo, Long >{

    int deleteByPrimaryKey(String contractAddress);

    int insert(ContractAddressInfo record);

    int insertSelective(ContractAddressInfo record);

    ContractAddressInfo selectByPrimaryKey(String contractAddress);

    int updateByPrimaryKeySelective(ContractAddressInfo record);

    int updateByPrimaryKey(ContractAddressInfo record);
}