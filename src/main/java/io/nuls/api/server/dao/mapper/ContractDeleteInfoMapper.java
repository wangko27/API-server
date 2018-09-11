package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.server.dao.util.Searchable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractDeleteInfoMapper extends BaseMapper<ContractDeleteInfo, Long >{

    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractDeleteInfo> list);

    /**
     * 删除合约
     * @param address
     * @return
     */
    int deleteContract(String address);

    /**
     * 统计数量
     * @param searchable
     * @return
     */
    int selectCountSearchable(Searchable searchable);

    ContractDeleteInfo selectByPrimaryKey(String id);
}