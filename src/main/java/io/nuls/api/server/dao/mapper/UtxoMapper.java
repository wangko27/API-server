package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.Utxo;
import io.nuls.api.entity.UtxoKey;
import io.nuls.api.server.dto.UtxoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface UtxoMapper extends BaseMapper<Utxo, UtxoKey>{
    /**
     * 持币账户统计
     * @return
     */
    List<UtxoDto> getBlockSumTxamount();

    int insertByBatch(@Param("list") List<Utxo> list);
}