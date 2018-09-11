package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractAddressInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractAddressInfoMapper extends BaseMapper<ContractAddressInfo, Long >{
    int deleteByPrimaryKey(String contractAddress);

    int insert(ContractAddressInfo record);

    int insertSelective(ContractAddressInfo record);

    ContractAddressInfo selectByPrimaryKey(String contractAddress);

    int updateByPrimaryKeySelective(ContractAddressInfo record);

    int updateByPrimaryKey(ContractAddressInfo record);

    int insertByBatch(@Param("list") List<ContractAddressInfo> list);
}