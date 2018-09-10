package io.nuls.api.server.business;

import io.nuls.api.entity.ContrackAddressInfo;
import io.nuls.api.server.dao.mapper.ContrackAddressInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @desription: 智能合约地址处理器，包括合约地址的回滚、查询与存储等
 * @author: qinyf
 * @date: 2018/9/10
 */
@Service
public class ContrackAddressBusiness implements BaseService<ContrackAddressInfo, String> {

    @Autowired
    private ContrackAddressInfoMapper contrackAddressInfoMapper;

    /**
     * 保存智能合约地址
     *
     * @param contrackAddressInfo 实体
     * @return 1成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(ContrackAddressInfo contrackAddressInfo) {
        return contrackAddressInfoMapper.insert(contrackAddressInfo);
        
    }

    /**
     * 更新智能合约地址
     *
     * @param contrackAddressInfo 实体
     * @return 1成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(ContrackAddressInfo contrackAddressInfo) {
        return contrackAddressInfoMapper.updateByPrimaryKey(contrackAddressInfo);
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
        return contrackAddressInfoMapper.deleteByPrimaryKey(contractAddress);
    }

    /**
     * 根据智能合约地址查询
     *
     * @param contractAddress 智能合约地址
     * @return
     */
    @Override
    public ContrackAddressInfo getByKey(String contractAddress) {
        return contrackAddressInfoMapper.selectByPrimaryKey(contractAddress);
    }
}
