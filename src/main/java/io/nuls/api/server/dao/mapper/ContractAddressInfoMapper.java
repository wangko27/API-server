package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractAddressInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractAddressInfoMapper extends BaseMapper<ContractAddressInfo, String> {
    int deleteByPrimaryKey(String contractAddress);

    ContractAddressInfo selectByPrimaryKey(String contractAddress);

    int insertByBatch(@Param("list") List<ContractAddressInfo> list);
}