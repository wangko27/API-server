package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.TransactionRelation;
import io.nuls.api.server.dao.util.Searchable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface TransactionRelationMapper extends BaseMapper<TransactionRelation, Long> {
    void deleteByTxHash(@Param("txHash") String txHash);

    int insertByBatch(@Param("list") List<TransactionRelation> list);

    void deleteList(@Param("list") List<String> txHashList);

    long isAddressExist(Searchable searchable);
}