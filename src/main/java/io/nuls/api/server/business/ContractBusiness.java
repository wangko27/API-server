package io.nuls.api.server.business;

import io.nuls.api.constant.ContractConstant;
import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.entity.ContractCallInfo;
import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.server.dao.mapper.ContractAddressInfoMapper;
import io.nuls.api.server.dao.mapper.ContractCallInfoMapper;
import io.nuls.api.server.dao.mapper.ContractDeleteInfoMapper;
import io.nuls.api.server.dao.mapper.ContractDeleteInfoMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 别名
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Service
public class ContractBusiness implements BaseService<ContractDeleteInfo, String> {

    @Autowired
    private ContractDeleteInfoMapper contractDeleteInfoMapper;
    @Autowired
    private ContractAddressInfoMapper contractAddressInfoMapper;
    @Autowired
    private ContractCallInfoMapper contractCallInfoMapper;

    /**
     * 根据地址获取别名
     *
     * @param address 账户地址
     * @return
     */
    public ContractDeleteInfo  getContractDeleteInfoByAddress(String address) {
        ContractDeleteInfo contractDeleteInfo = null;
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        contractDeleteInfo = contractDeleteInfoMapper.selectBySearchable(searchable);
        return contractDeleteInfo;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllDelInfo(List<ContractDeleteInfo> list) {
        int i = 0;
        if (list.size() > 0) {
            i = contractDeleteInfoMapper.insertByBatch(list);
        }
        return i;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllCallInfo(List<ContractCallInfo> list) {
        int i = 0;
        if (list.size() > 0) {
//            i = contractCallInfoMapper.insertByBatch(list);
        }
        return i;
    }

    @Override
    public int save(ContractDeleteInfo contractDeleteInfo) {
        return 0;
    }

    @Override
    public int update(ContractDeleteInfo contractDeleteInfo) {
        return 0;
    }

    @Override
    public int deleteByKey(String s) {
        return 0;
    }

    public int deleteContract(String address) {
        ContractAddressInfo info = new ContractAddressInfo();
        info.setContractAddress(address);
        info.setStatus(ContractConstant.CONTRACT_STATUS_DELETED);
        return contractAddressInfoMapper.updateByPrimaryKeySelective(info);
    }

    /**
     * 根据主键获取别名
     *
     * @param id
     * @return
     */
    @Override
    public ContractDeleteInfo getByKey(String id) {
        return contractDeleteInfoMapper.selectByPrimaryKey(id);
    }

    /**
     * 回滚删除合约交易
     * @param hash
     */
    public void rollbackContractDeleteInfo(String hash) {
        //根据hash查出删除交易，获取到合约地址，变更合约状态为正常，最后删除该交易
    }
}
