package io.nuls.api.server.dao.mapper;

import io.nuls.api.entity.ContractTokenTransferInfo;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.contract.ContractTokenTransferInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@MyBatisMapper
public interface ContractTokenTransferInfoMapper extends BaseMapper<ContractTokenTransferInfo, String>{

    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertByBatch(@Param("list") List<ContractTokenTransferInfo> list);

    /**
     * 批量删除
     * @param txHashList
     */
    void deleteList(@Param("list") List<String> txHashList);

    /**
     * get total transfers
     * @param searchable
     * @return
     */
    long selectTotalTransfer(Searchable searchable);

    /**
     * 根据NULS钱包地址获取代币转账信息
     * @param accountAddress
     * @return
     */
    List<ContractTokenTransferInfoDto> selectTransferDtos(String accountAddress);
}