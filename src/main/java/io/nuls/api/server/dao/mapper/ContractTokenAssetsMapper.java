package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractTokenAssets;
import io.nuls.api.entity.ContractTokenTransferInfo;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.contract.ContractTokenAssetsDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
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

    /**
     * 获取详情
     * @param searchable
     */
    ContractTokenAssetsDetail getDetail(@Param("list") Searchable searchable);
    /**
     * get total holders
     * @param searchable
     * @return
     */
    long selectTotalHolders(Searchable searchable);
}