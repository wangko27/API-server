package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractCreateInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractCreateInfoMapper extends BaseMapper<ContractCreateInfo, String>{
    int deleteByPrimaryKey(String createTxHash);

    int insert(ContractCreateInfo record);

    int insertSelective(ContractCreateInfo record);

    ContractCreateInfo selectByPrimaryKey(String createTxHash);

    int updateByPrimaryKeySelective(ContractCreateInfo record);

    int updateByPrimaryKey(ContractCreateInfo record);

    int insertByBatch(@Param("list") List<ContractCreateInfo> list);

    void deleteList(@Param("list") List<String> txHashList);
}