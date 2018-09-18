package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractTransaction;
import io.nuls.api.server.dao.util.Searchable;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisMapper
public interface ContractTransactionMapper extends BaseMapper<ContractTransaction, String>{
    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractTransaction> list);

    /**
     * 批量删除
     * @param txHashList
     */
    void deleteList(@Param("list") List<String> txHashList);

    List<ContractTransaction> selectContractTxList(Map map);

    int selectTotalCount(Searchable searchable);
}