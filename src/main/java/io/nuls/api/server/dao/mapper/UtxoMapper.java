package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.Utxo;
import io.nuls.api.entity.UtxoKey;

@MyBatisMapper
public interface UtxoMapper {
    int deleteByPrimaryKey(UtxoKey key);

    int insert(Utxo record);

    int insertSelective(Utxo record);

    Utxo selectByPrimaryKey(UtxoKey key);

    int updateByPrimaryKeySelective(Utxo record);

    int updateByPrimaryKey(Utxo record);
}