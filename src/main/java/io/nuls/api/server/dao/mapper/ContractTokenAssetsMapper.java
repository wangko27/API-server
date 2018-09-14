package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractTokenAssets;
import io.nuls.api.entity.ContractTokenTransferInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContractTokenAssetsMapper extends BaseMapper<ContractTokenAssets, String>{
    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractTokenAssets> list);

    /**
     * 批量删除
     * @param txHashList
     */
    void deleteList(@Param("list") List<String> txHashList);
}