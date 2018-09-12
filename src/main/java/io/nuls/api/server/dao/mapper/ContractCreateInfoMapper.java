package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractCreateInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractCreateInfoMapper extends BaseMapper<ContractCreateInfo, String>{

    int insertByBatch(@Param("list") List<ContractCreateInfo> list);

    void deleteList(@Param("list") List<String> txHashList);
}