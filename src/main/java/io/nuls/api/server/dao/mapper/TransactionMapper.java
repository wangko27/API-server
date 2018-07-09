package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.Transaction;
import io.nuls.api.server.dao.util.Searchable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface TransactionMapper extends BaseMapper<Transaction, Long>{
    //todo 先分页查询relation中的数据，然后根据hash去leveldb查询
    List<Transaction> selectListByAddress(Searchable searchable);

    int insertByBatch(@Param("list") List<Transaction> list);
}