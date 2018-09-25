package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractResultInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractResultInfoMapper extends BaseMapper<ContractResultInfo, String >{
    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractResultInfo> list);

    /**
     * 批量删除
     * @param txHashList
     */
    void deleteList(@Param("list") List<String> txHashList);
}