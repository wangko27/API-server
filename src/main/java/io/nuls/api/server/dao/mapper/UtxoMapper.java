package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.Utxo;
import io.nuls.api.entity.UtxoKey;

@MyBatisMapper
public interface UtxoMapper extends BaseMapper<Utxo, UtxoKey>{
}