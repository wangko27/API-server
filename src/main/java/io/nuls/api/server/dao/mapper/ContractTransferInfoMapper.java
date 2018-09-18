package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractTransferInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractTransferInfoMapper extends BaseMapper<ContractTransferInfo, String> {
    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractTransferInfo> list);

    /**
     * 批量删除
     *
     * @param txHashList
     */
    void deleteList(@Param("list") List<String> txHashList);

}