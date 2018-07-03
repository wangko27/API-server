package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.TransactionRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface TransactionRelationMapper {

    int deleteByPrimaryKey(Long id);

    int insert(TransactionRelation record);

    int insertSelective(TransactionRelation record);

    TransactionRelation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TransactionRelation record);

    int updateByPrimaryKey(TransactionRelation record);

    void deleteByTxHash(@Param("txHash") String txHash);

    int insertByBatch(@Param("list") List<TransactionRelation> list);
}