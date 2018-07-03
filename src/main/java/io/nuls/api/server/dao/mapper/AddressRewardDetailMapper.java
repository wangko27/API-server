package io.nuls.api.server.dao.mapper;


import io.nuls.api.entity.AddressRewardDetail;
import io.nuls.api.server.dao.util.Searchable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface AddressRewardDetailMapper extends BaseMapper<AddressRewardDetail, Long> {
    /**
     * 统计总的奖励
     * @param searchable
     * @return
     */
    Long selectSumReward(Searchable searchable);

    Long getLastRewardHeight(Searchable searchable);

    int insertByBatch(@Param("list") List<AddressRewardDetail> list);

}