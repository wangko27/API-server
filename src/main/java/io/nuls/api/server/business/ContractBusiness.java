package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mchange.v1.util.ListUtils;
import io.nuls.api.constant.ContractConstant;
import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.entity.ContractCallInfo;
import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.entity.ContractResultInfo;
import io.nuls.api.entity.ContractTokenInfo;
import io.nuls.api.entity.ContractTransaction;
import io.nuls.api.model.ContractTokenTransferDto;
import io.nuls.api.server.dao.mapper.ContractAddressInfoMapper;
import io.nuls.api.server.dao.mapper.ContractCallInfoMapper;
import io.nuls.api.server.dao.mapper.ContractDeleteInfoMapper;
import io.nuls.api.server.dao.mapper.ContractResultInfoMapper;
import io.nuls.api.server.dao.mapper.ContractTokenInfoMapper;
import io.nuls.api.server.dao.mapper.ContractTransactionMapper;
import io.nuls.api.entity.*;
import io.nuls.api.server.dao.mapper.*;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 别名
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Service
public class ContractBusiness implements BaseService<ContractDeleteInfo, String> {

    @Autowired
    private ContractAddressInfoMapper contractAddressInfoMapper;
    @Autowired
    private ContractCreateInfoMapper contractCreateInfoMapper;
    @Autowired
    private ContractCallInfoMapper contractCallInfoMapper;
    @Autowired
    private ContractDeleteInfoMapper contractDeleteInfoMapper;
    @Autowired
    private ContractResultInfoMapper contractResultInfoMapper;
    @Autowired
    private ContractTokenInfoMapper contractTokenInfoMapper;
    @Autowired
    private ContractTransactionMapper contractTransactionMapper;
    @Autowired
    private ContractTokenTransferInfoMapper contractTokenTransferInfoMapper;
    @Autowired
    private ContractTokenAssetsMapper contractTokenAssetsMapper;


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
            i = contractCallInfoMapper.insertByBatch(list);
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
     * @param txHashList
     */
    public void rollbackContractDeleteInfo(List<String> txHashList) {
        for (String hash : txHashList) {
            //变更合约状态为正常，最后删除该交易
            ContractAddressInfo contractAddressInfo = new ContractAddressInfo();
            contractAddressInfo.setCreateTxHash(hash);
            contractAddressInfo.setStatus(ContractConstant.CONTRACT_STATUS_CONFIRMED);
            contractAddressInfoMapper.updateByPrimaryKey(contractAddressInfo);
            Searchable searchable = new Searchable();
            searchable.addCondition("create_tx_hash", SearchOperator.eq, hash);
            contractDeleteInfoMapper.deleteBySearchable(searchable);
        }

    }

    /**
     * 回滚调用合约交易
     * @param txHashList
     */
    public void rollbackContractCallInfo(List<String> txHashList) {
        if(null != txHashList && txHashList.size() > 0){
            contractResultInfoMapper.deleteList(txHashList);
            contractCallInfoMapper.deleteList(txHashList);
        }
    }

    /**
     * 回滚代币转账交易记录
     * @param txHashList
     */
    public void rollbackContractTokenTransferInfo(List<String> txHashList) {
        if(null != txHashList && txHashList.size() > 0){
            contractTokenTransferInfoMapper.deleteList(txHashList);
        }
    }

    /**
     * 批量保存智能合约地址
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllContractAddress(List<ContractAddressInfo> list) {
        if (list.size() > 0) {
            return contractAddressInfoMapper.insertByBatch(list);
        }
        return 0;
    }

    /**
     * 删除智能合约地址，根据高度删除，只有回滚的时候才会调用
     *
     * @param height
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteContractByHeight(long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return contractAddressInfoMapper.deleteBySearchable(searchable);
    }

    /**
     * 批量保存智能合约创建交易过程数据
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllCreateData(List<ContractCreateInfo> list) {
        if (list.size() > 0) {
            return contractCreateInfoMapper.insertByBatch(list);
        }
        return 0;
    }

    /**
     * 删除智能合约创建过程数据，根据交易哈希批量删除，只有回滚的时候才会调用
     *
     * @param txHashList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteCreateDataList(List<String> txHashList) {
        if(null != txHashList && txHashList.size() > 0){
            contractCreateInfoMapper.deleteList(txHashList);
        }
    }

    /**
     * 智能合约交易执行结果
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllContractResult(List<ContractResultInfo> list) {
        int i = 0;
        if (list.size() > 0) {
            i = contractResultInfoMapper.insertByBatch(list);
        }
        return i;
    }

    /**
     * 删除智能合约交易执行结果，根据交易哈希批量删除，只有回滚的时候才会调用
     *
     * @param txHashList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteContractResultList(List<String> txHashList) {
        if(null != txHashList && txHashList.size() > 0){
            contractResultInfoMapper.deleteList(txHashList);
        }
    }

    /**
     * 批量保存token代币信息
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllToken(List<ContractTokenInfo> list) {
        int i = 0;
        if (list.size() > 0) {
            i = contractTokenInfoMapper.insertByBatch(list);
        }
        return i;
    }

    /**
     * 批量保存token代币转账信息
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllTokenTransferInfo(List<ContractTokenTransferInfo> list) {
        int i = 0;
        if (list.size() > 0) {
            i = contractTokenTransferInfoMapper.insertByBatch(list);
        }
        return i;
    }

    /**
     * 删除智能合约token代币信息，根据交易哈希批量删除，只有回滚的时候才会调用
     *
     * @param txHashList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteTokenList(List<String> txHashList) {
        if(null != txHashList && txHashList.size() > 0){
            contractTokenInfoMapper.deleteList(txHashList);
        }
    }

    /**
     * 批量保存智能合约交易记录
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllTransaction(List<ContractTransaction> list) {
        int i = 0;
        if (list.size() > 0) {
            i = contractTransactionMapper.insertByBatch(list);
        }
        return i;
    }

    /**
     * 删除智能合约交易记录，根据交易哈希批量删除，只有回滚的时候才会调用
     *
     * @param txHashList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteTransactionList(List<String> txHashList) {
        if(null != txHashList && txHashList.size() > 0){
            contractTransactionMapper.deleteList(txHashList);
        }
    }

    /**
     * 获取代币转账信息
     *
     * @param address    合约地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<ContractTokenTransferInfo> getContractTokenTransfers(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            if (StringUtils.validAddress(address)) {
                searchable.addCondition("contract_address", SearchOperator.eq, address);
            } else {
                return null;
            }
        }
        PageHelper.orderBy("create_time desc");
        PageInfo<ContractTokenTransferInfo> page = new PageInfo<ContractTokenTransferInfo>(contractTokenTransferInfoMapper.selectList(searchable));
        return page;
    }

    /**
     * 获取代币转账信息
     *
     * @param address    合约地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<ContractTokenAssets> getContractTokenAssets(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            if (StringUtils.validAddress(address)) {
                searchable.addCondition("contract_address", SearchOperator.eq, address);
            } else {
                return null;
            }
        }
        PageHelper.orderBy("amount asc");
        PageInfo<ContractTokenAssets> page = new PageInfo<ContractTokenAssets>(contractTokenAssetsMapper.selectList(searchable));
        return page;
    }

    public void calContractTokenAssets(List<ContractTokenTransferInfo> contractTokenTransferDtos, String contractAddress) {
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(contractAddress)) {
            if (StringUtils.validAddress(contractAddress)) {
                searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
            } else {
                return;
            }
        }

        List<ContractTokenAssets> contractTokenAssets = contractTokenAssetsMapper.selectList(searchable);
        HashMap<String, Long> tempMap = new HashMap<>();
        for (ContractTokenTransferInfo contractTokenTransferInfo : contractTokenTransferDtos) {
            String fromAddress = contractTokenTransferInfo.getFromAddress();
            String toAddress = contractTokenTransferInfo.getToAddress();
            Long txValue = contractTokenTransferInfo.getTxValue();
            Long fromAmount = tempMap.get("fromAddress") != null ? tempMap.get("fromAddress") : 0L;
            Long toAmount = tempMap.get("toAddress") != null ? tempMap.get("toAddress") : 0L;
            fromAmount -= txValue;
            toAmount += txValue;
            tempMap.put(fromAddress, fromAmount);
            tempMap.put(toAddress, toAmount);
        }
        for (Map.Entry<String, Long> stringLongEntry : tempMap.entrySet()) {

        }

    }
}
