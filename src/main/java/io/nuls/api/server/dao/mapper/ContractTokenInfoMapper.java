package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractTokenInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractTokenInfoMapper extends BaseMapper<ContractTokenInfo, String>{

    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractTokenInfo> list);

    /**
     * 批量删除
     * @param txHashList
     */
    void deleteList(@Param("list") List<String> txHashList);
}