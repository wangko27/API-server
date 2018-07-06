package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.UtxoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface UtxoMapper extends BaseMapper<Utxo, Long>{
    /**
     * 持币账户统计
     * @return
     */
    List<UtxoDto> getBlockSumTxamount();

    int insertByBatch(@Param("list") List<Utxo> list);

    /**
     * 根据条件查询Utxo
     * @param searchable
     * @return
     */
    Utxo selectByHashAndIndex(Searchable searchable);

    /**
     * 根据条件删除Utxo
     * @param searchable
     * @return
     */
    int deleteByHashAndIndex(Searchable searchable);

    /**
     * 根据list修改
     * @param list
     * @return
     */
    int updateByBatch(@Param("list") List<Utxo> list);


}