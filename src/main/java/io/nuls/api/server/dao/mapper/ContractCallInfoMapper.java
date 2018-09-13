package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.entity.ContractCallInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractCallInfoMapper extends BaseMapper<ContractCallInfo, Long >{

    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractCallInfo> list);

    int deleteByPrimaryKey(String contractAddress);

    int insert(ContractCallInfo record);

    int insertSelective(ContractCallInfo record);

    ContractCallInfo selectByPrimaryKey(String contractAddress);

    int updateByPrimaryKeySelective(ContractCallInfo record);

    int updateByPrimaryKey(ContractCallInfo record);
}