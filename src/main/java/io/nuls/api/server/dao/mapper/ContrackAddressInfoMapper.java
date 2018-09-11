package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContrackAddressInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContrackAddressInfoMapper extends BaseMapper<ContrackAddressInfo, String>{
    int deleteByPrimaryKey(String contractAddress);

    int insert(ContrackAddressInfo record);

    int insertSelective(ContrackAddressInfo record);

    ContrackAddressInfo selectByPrimaryKey(String contractAddress);

    int updateByPrimaryKeySelective(ContrackAddressInfo record);

    int updateByPrimaryKey(ContrackAddressInfo record);

    int insertByBatch(@Param("list") List<ContrackAddressInfo> list);

}