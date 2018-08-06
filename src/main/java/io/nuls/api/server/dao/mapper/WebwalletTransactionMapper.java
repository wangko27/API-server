package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.WebwalletTransaction;
import org.apache.ibatis.annotations.Param;

@MyBatisMapper
public interface WebwalletTransactionMapper extends BaseMapper<WebwalletTransaction,String> {
    int updateStatusByPrimaryKey(@Param("hash") String hash);
    //删除以及确认了的交易
    int deleteConfirmTx();
}