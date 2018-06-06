package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.AddressRewardDetail;
import io.nuls.api.server.dao.util.Searchable;

@MyBatisMapper
public interface AddressRewardDetailMapper extends BaseMapper<AddressRewardDetail, Long> {
    /**
     * 统计总的奖励
     * @param searchable
     * @return
     */
    Long selectSumReward(Searchable searchable);

    Long getLastRewardHeight(Searchable searchable);
}