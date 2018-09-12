package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.Alias;
import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.entity.ContractResultInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractResultInfoMapper extends BaseMapper<Alias, String>{
    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractResultInfo> list);

    int insert(ContractResultInfo record);

    int insertSelective(ContractResultInfo record);
}