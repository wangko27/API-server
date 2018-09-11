package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContrackCreateInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContrackCreateInfoMapper extends BaseMapper<ContrackCreateInfo, String>{
    int deleteByPrimaryKey(String createTxHash);

    int insert(ContrackCreateInfo record);

    int insertSelective(ContrackCreateInfo record);

    ContrackCreateInfo selectByPrimaryKey(String createTxHash);

    int updateByPrimaryKeySelective(ContrackCreateInfo record);

    int updateByPrimaryKey(ContrackCreateInfo record);

    int insertByBatch(@Param("list") List<ContrackCreateInfo> list);

    void deleteList(@Param("list") List<String> txHashList);
}