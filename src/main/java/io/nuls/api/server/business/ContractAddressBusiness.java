package io.nuls.api.server.business;

import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.server.dao.mapper.ContractAddressInfoMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @desription: 智能合约地址处理器，包括合约地址的回滚、查询与存储等
 * @author: qinyf
 * @date: 2018/9/10
 */
@Service
public class ContractAddressBusiness implements BaseService<ContractAddressInfo, String> {

    @Autowired
    private ContractAddressInfoMapper ContractAddressInfoMapper;

    /**
     * 保存智能合约地址
     *
     * @param ContractAddressInfo 实体
     * @return 1成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(ContractAddressInfo ContractAddressInfo) {
        return ContractAddressInfoMapper.insert(ContractAddressInfo);
        
    }

    /**
     * 批量保存
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAll(List<ContractAddressInfo> list) {
        if (list.size() > 0) {
            return ContractAddressInfoMapper.insertByBatch(list);
        }
        return 0;
    }

    /**
     * 更新智能合约地址
     *
     * @param ContractAddressInfo 实体
     * @return 1成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(ContractAddressInfo ContractAddressInfo) {
        return ContractAddressInfoMapper.updateByPrimaryKey(ContractAddressInfo);
    }

    /**
     * 根据主键-智能合约地址删除节点
     *
     * @param contractAddress 主键
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(String contractAddress) {
        return ContractAddressInfoMapper.deleteByPrimaryKey(contractAddress);
    }

    /**
     * 根据智能合约地址查询
     *
     * @param contractAddress 智能合约地址
     * @return
     */
    @Override
    public ContractAddressInfo getByKey(String contractAddress) {
        return ContractAddressInfoMapper.selectByPrimaryKey(contractAddress);
    }

    /**
     * 删除智能合约地址，根据高度删除，只有回滚的时候才会调用
     *
     * @param height
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHeight(long height) {
        System.out.println("deleteByHeight====="+height);
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return ContractAddressInfoMapper.deleteBySearchable(searchable);
    }
}
