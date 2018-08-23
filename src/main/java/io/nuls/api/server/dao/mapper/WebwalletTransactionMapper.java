package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.WebwalletTransaction;
import io.nuls.api.server.dao.util.Searchable;

@MyBatisMapper
public interface WebwalletTransactionMapper extends BaseMapper<WebwalletTransaction,String> {
    //int updateStatusByPrimaryKey(@Param("hash") String hash);
    //删除以及确认了的交易
    int deleteConfirmTx();

    //根据条件查询数量
    int selectCountBySearch(Searchable searchable);
}