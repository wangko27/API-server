package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.ContractConstant;
import io.nuls.api.entity.Balance;
import io.nuls.api.entity.ContractAddressInfo;
import io.nuls.api.entity.ContractCallInfo;
import io.nuls.api.entity.ContractCreateInfo;
import io.nuls.api.entity.ContractDeleteInfo;
import io.nuls.api.entity.ContractResultInfo;
import io.nuls.api.entity.ContractTokenAssets;
import io.nuls.api.entity.ContractTokenInfo;
import io.nuls.api.entity.ContractTokenTransferInfo;
import io.nuls.api.entity.ContractTransaction;
import io.nuls.api.entity.ContractTransferInfo;
import io.nuls.api.entity.Transaction;
import io.nuls.api.server.dao.mapper.ContractAddressInfoMapper;
import io.nuls.api.server.dao.mapper.ContractCallInfoMapper;
import io.nuls.api.server.dao.mapper.ContractCreateInfoMapper;
import io.nuls.api.server.dao.mapper.ContractDeleteInfoMapper;
import io.nuls.api.server.dao.mapper.ContractResultInfoMapper;
import io.nuls.api.server.dao.mapper.ContractTokenAssetsMapper;
import io.nuls.api.server.dao.mapper.ContractTokenInfoMapper;
import io.nuls.api.server.dao.mapper.ContractTokenTransferInfoMapper;
import io.nuls.api.server.dao.mapper.ContractTransactionMapper;
import io.nuls.api.server.dao.mapper.ContractTransferInfoMapper;
import io.nuls.api.server.dao.mapper.TransactionRelationMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.ContractTokenDto;
import io.nuls.api.server.dto.contract.ContractAddressInfoDto;
import io.nuls.api.server.dto.contract.ContractTokenAssetsDetail;
import io.nuls.api.server.dto.contract.ContractTransactionDetailDto;
import io.nuls.api.server.dto.contract.*;
import io.nuls.api.server.dto.contract.vm.ProgramMethod;
import io.nuls.api.utils.JSONUtils;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private ContractTransferInfoMapper contractTransferInfoMapper;
    @Autowired
    private TransactionRelationMapper transactionRelationMapper;
    @Autowired
    private BalanceBusiness balanceBusiness;

    /**
     * 根据地址获取别名
     *
     * @param address 账户地址
     * @return
     */
    public ContractDeleteInfo getContractDeleteInfoByAddress(String address) {
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
            Searchable searchable = new Searchable();
            searchable.addCondition("create_tx_hash", SearchOperator.eq, hash);
            //变更合约状态为正常，最后删除该交易
            ContractAddressInfo contractAddressInfo = contractAddressInfoMapper.selectBySearchable(searchable);
            contractAddressInfo.setStatus(ContractConstant.CONTRACT_STATUS_NORMAL);
            contractAddressInfoMapper.updateByPrimaryKey(contractAddressInfo);
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
        //一个Block包含多笔交易，每一笔交易可能包含多笔代币转账交易
        if(null != txHashList && txHashList.size() > 0){
            for (String hash : txHashList) {
                Searchable searchable = new Searchable();
                searchable.addCondition("create_tx_hash", SearchOperator.eq, hash);
                ContractAddressInfo contractAddressInfo = contractAddressInfoMapper.selectBySearchable(searchable);
                List<ContractTokenTransferInfo> contractTokenTransferInfoList = contractTokenTransferInfoMapper.selectList(searchable);
                calContractTokenAssets(contractTokenTransferInfoList, contractAddressInfo.getContractAddress(), true);
            }
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
     * 批量保存合约内部转账交易
     *
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllTransferInfo(List<ContractTransferInfo> list) {
        int i = 0;
        if (list.size() > 0) {
            i = contractTransferInfoMapper.insertByBatch(list);
        }
        return i;
    }

    /**
     * 批量删除合约内部转账交易
     *
     * @param list
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteAllTransferInfo(List<String> list) {
        int i = 0;
        if (list.size() > 0) {
            contractTransferInfoMapper.deleteList(list);
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
     * 根据合约地址获取代币转账信息
     *
     * @param address    合约地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<ContractTokenTransferInfoDto> getContractTokenTransferInfosByContractAddress(String address, int pageNumber, int pageSize) {
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
        List<ContractTokenTransferInfo> contractTokenTransferInfos = contractTokenTransferInfoMapper.selectList(searchable);
        ContractTokenInfo contractTokenInfo = contractTokenInfoMapper.selectBySearchable(searchable);
        PageInfo<ContractTokenTransferInfoDto> page = new PageInfo<>(ContractTokenTransferInfoDto.parseList(contractTokenTransferInfos, contractTokenInfo));
        return page;
    }

    /**
     * 根据NULS钱包地址获取代币转账信息
     *
     * @param accountAddress    NULS钱包地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<ContractTokenTransferInfoDto> getContractTokenTransferInfosByAccountAddress(String accountAddress, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        if (StringUtils.isNotBlank(accountAddress)) {
            if (StringUtils.validAddress(accountAddress)) {

            } else {
                return null;
            }
        }
        PageHelper.orderBy("create_time asc");
        PageInfo<ContractTokenTransferInfoDto> page = new PageInfo<>(contractTokenTransferInfoMapper.selectTransferDtos(accountAddress));
        return page;
    }

    /**
     * 获取某个代币的持有者列表
     *
     * @param address    合约地址
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<ContractTokenAssetsDto> getContractTokenAssets(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            if (StringUtils.validAddress(address)) {
                searchable.addCondition("contract_address", SearchOperator.eq, address);
            } else {
                return null;
            }
        }
        PageHelper.orderBy("amount desc");
        List<ContractTokenAssets> contractTokenAssets = contractTokenAssetsMapper.selectList(searchable);
        PageInfo<ContractTokenAssetsDto> page = new PageInfo<>(ContractTokenAssetsDto.parseList(contractTokenAssets));
        return page;
    }

    /**
     * 获取某个账户的代币详情信息
     *
     * @param address    账户地址
     * @param contractAddress    合约地址
     * @return
     */
    public ContractTokenAssetsDetail getContractTokenAssetsDetails(String address, String contractAddress) {
        ContractTokenAssets assets = new ContractTokenAssets();
        ContractTokenAssetsDetail detail = new ContractTokenAssetsDetail();
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address) && StringUtils.isNotBlank(contractAddress)) {
            if (StringUtils.validAddress(address) && StringUtils.validAddress(contractAddress)) {
                searchable.addCondition("account_address", SearchOperator.eq, address);
                searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
            } else {
                return null;
            }
        }
        assets = contractTokenAssetsMapper.selectBySearchable(searchable);
        detail.setAccountAddress(assets.getAccountAddress());
        detail.setAmount(assets.getAmount());
        detail.setContractAddress(assets.getContractAddress());
        detail.setHash(assets.getHash());

        searchable = new Searchable();
        searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
        searchable.addCondition("from_address", SearchOperator.eq, address);
        List<ContractTokenTransferInfo> temp1 = contractTokenTransferInfoMapper.selectList(searchable);
        searchable = new Searchable();
        searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
        searchable.addCondition("to_address", SearchOperator.eq, address);
        List<ContractTokenTransferInfo> temp2 = contractTokenTransferInfoMapper.selectList(searchable);
        temp1.addAll(temp2);
        detail.setList(temp1);
        return detail;
    }

    /**
     * 计算代币资产
     * @param contractTokenTransferDtos     代币转账记录
     * @param contractAddress       代币合约地址
     * @param rollback       是否是回滚操作
     */
    public void calContractTokenAssets(List<ContractTokenTransferInfo> contractTokenTransferDtos, String contractAddress, boolean rollback) {
        BigInteger sign = new BigInteger("1");
        if (rollback) {
            sign = new BigInteger("-1");
        }
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(contractAddress)) {
            if (StringUtils.validAddress(contractAddress)) {
                searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
            } else {
                return;
            }
        }

        List<ContractTokenAssets> contractTokenAssets = contractTokenAssetsMapper.selectList(searchable);
        List<ContractTokenAssets> addContractTokenAssets = new ArrayList<>();
        List<String> deleteContractTokenAssets = new ArrayList<>();
        HashMap<String, BigInteger> tempMap = new HashMap<>();
        for (ContractTokenTransferInfo contractTokenTransferInfo : contractTokenTransferDtos) {
            String fromAddress = contractTokenTransferInfo.getFromAddress();
            String toAddress = contractTokenTransferInfo.getToAddress();
            BigInteger txValue = contractTokenTransferInfo.getTxValue().multiply(sign);
            if (StringUtils.isNotBlank(fromAddress)) {
                BigInteger fromAmount = tempMap.get(fromAddress) != null ? tempMap.get(fromAddress) : new BigInteger("0");
                fromAmount = fromAmount.subtract(txValue);
                tempMap.put(fromAddress, fromAmount);
            }
            BigInteger toAmount = tempMap.get(toAddress) != null ? tempMap.get(toAddress) : new BigInteger("0");
            toAmount = toAmount.add(txValue);
            tempMap.put(toAddress, toAmount);
        }
        for (Map.Entry<String, BigInteger> stringLongEntry : tempMap.entrySet()) {
            String address = stringLongEntry.getKey();
            BigInteger value = stringLongEntry.getValue();
            boolean exist = false;
            for (ContractTokenAssets contractTokenAsset : contractTokenAssets) {
                if (address.equals(contractTokenAsset.getAccountAddress())) {
                    exist = true;
                    BigInteger amount = new BigInteger(contractTokenAsset.getAmount());
                    amount = amount.add(value);
                    if (amount.compareTo(BigInteger.ZERO) == 1) {
                        contractTokenAsset.setAmount(amount.toString());
                        addContractTokenAssets.add(contractTokenAsset);
                    } else {
                        deleteContractTokenAssets.add(contractTokenAsset.getHash());
                    }
                }
            }
            if (!exist) {
                ContractTokenAssets contractTokenAsset = new ContractTokenAssets();
                contractTokenAsset.setContractAddress(contractAddress);
                contractTokenAsset.setAccountAddress(address);
                contractTokenAsset.setAmount(value.toString());
                contractTokenAsset.setHash(StringUtils.getNewUUID());
                addContractTokenAssets.add(contractTokenAsset);
            }
        }
        saveAllContractTokenAssets(addContractTokenAssets);
        deleteAllContractTokenAssets(deleteContractTokenAssets);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAllContractTokenAssets(List<ContractTokenAssets> list) {
        int i = 0;
        if (list.size() > 0) {
            i = contractTokenAssetsMapper.insertByBatch(list);
        }
        return i;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteAllContractTokenAssets(List<String> list) {
        int i = 0;
        if (list.size() > 0) {
            contractTokenAssetsMapper.deleteList(list);
        }
    }

    /**
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<ContractAddressInfo> getContractInfoList(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        PageHelper.orderBy("create_time desc");
        PageInfo<ContractAddressInfo> page = new PageInfo<>(contractAddressInfoMapper.selectList(searchable));
        return page;
    }

    /**
     * @param contractAddress
     * @return
     */
    public ContractAddressInfoDto getContractInfo(String contractAddress) {
        ContractAddressInfo contractInfo = contractAddressInfoMapper.selectByPrimaryKey(contractAddress);
        ContractAddressInfoDto contract = null;
        if (contractInfo != null) {
            contract = new ContractAddressInfoDto(contractInfo);
            //查询合约地址余额
            Balance balance = balanceBusiness.getBalance(contract.getContractAddress());
            if (balance != null) {
                //设置余额
                contract.setBalance(balance.getUsable());
            }
            //如果是NRC20则查询NRC20信息
            if (ContractConstant.CONTRACT_NRC20_STATUS_YES == contract.getIsNrc20()) {
                ContractTokenInfo tokenInfo = contractTokenInfoMapper.selectByPrimaryKey(contract.getCreateTxHash());
                if (tokenInfo != null) {
                    contract.setTokenName(tokenInfo.getTokenName());
                    contract.setSymbol(tokenInfo.getSymbol());
                    contract.setDecimals(tokenInfo.getDecimals());
                    contract.setTotalsupply(tokenInfo.getTotalsupply());
                }
            }
            //查询交易笔数（非合约交易）
            Searchable searchable = new Searchable();
            searchable.addCondition("address", SearchOperator.eq, contractAddress);
            searchable.addCondition("type", SearchOperator.notIn, ContractConstant.TX_TYPE_CONTRACT_LIST);
            int txCount = transactionRelationMapper.selectTotalCount(searchable);
            //查询交易笔数（合约交易）
            searchable = new Searchable();
            searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
            txCount += contractTransactionMapper.selectTotalCount(searchable);
            contract.setTxCount(txCount);

            //查询合约创建详情
            ContractCreateInfo createInfo = contractCreateInfoMapper.selectByPrimaryKey(contract.getCreateTxHash());
            if (createInfo != null) {
                try {
                    List<ProgramMethod> methodList = JSONUtils.json2list(createInfo.getMethods(), ProgramMethod.class);
                    contract.setMethod(methodList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return contract;
    }

    /**
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<ContractTransaction> getContractTxList(String contractAddress,String accountAddress,int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Map<String,Object> params=new HashMap<>();
        params.put("contractAddress",contractAddress);
        params.put("accountAddress",accountAddress);
        PageInfo<ContractTransaction> page = new PageInfo<>(contractTransactionMapper.selectContractTxList(params));
        return page;
    }
    /**
     * Get all tokens list
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public  PageInfo<ContractTokenDto> getContractTokeninfoList(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber,pageSize);
        //Searchable searchable = new Searchable();
        PageInfo<ContractTokenDto> page = new PageInfo<>(contractTokenInfoMapper.getAll());
        return page;
    }

    /**
     * Get all tokens list
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public  PageInfo<ContractTokenDto> getContractTokeninfoListByAccountAddress(int pageNumber, int pageSize, String accountAddress) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(accountAddress)) {
            if (!StringUtils.validAddress(accountAddress)) {
                return null;
            }
        }
        PageHelper.orderBy("create_time desc");
        List<ContractTokenDto> contractTokenDtos = contractTokenInfoMapper.getListByAccountAddress(accountAddress);
        PageInfo<ContractTokenDto> page = new PageInfo<>(contractTokenDtos);
        return page;
    }

    /**
     * get contract token info by contract address
     * @param contractAddress
     * @return
     */
    public ContractTokenInfo getContractTokenInfo(String contractAddress) {
        return contractTokenInfoMapper.selectTokenByAddress(contractAddress);
    }

    /**
     * get contract address info by contract address
     * @param contractAddress
     * @return
     */
    public ContractAddressInfo getContractAddressInfo(String contractAddress) {
        return contractAddressInfoMapper.selectByPrimaryKey(contractAddress);
    }

    /**
     * get total transfers
     * @param contractAddress
     * @return
     */
    public long selectTotalTransfer(String contractAddress) {
        Searchable searchable = new Searchable();
        searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
        long result = contractTokenTransferInfoMapper.selectTotalTransfer(searchable);
        return result;
    }
    /**
     * get total holders
     * @param contractAddress
     * @return
     */
    public Long selectTotalHolders(String contractAddress) {
        Searchable searchable = new Searchable();
        searchable.addCondition("contract_address", SearchOperator.eq, contractAddress);
        long result = contractTokenAssetsMapper.selectTotalHolders(searchable);
        return result;
    }

    public ContractTransactionDetailDto getContractTransactionDetail(String hash) {
        Transaction transaction = transactionBusiness.getByHash(hash);
        Searchable searchable = new Searchable();
        searchable.addCondition("create_tx_hash", SearchOperator.eq, hash);
        switch (transaction.getType()) {
            case ContractConstant.TX_TYPE_CREATE_CONTRACT :
                ContractCreateInfo contractCreateInfo = contractCreateInfoMapper.selectBySearchable(searchable);
                transaction.setTxData(contractCreateInfo);
                break;
            case ContractConstant.TX_TYPE_CALL_CONTRACT :
                ContractCallInfo contractCallInfo = contractCallInfoMapper.selectBySearchable(searchable);
                transaction.setTxData(contractCallInfo);
                break;
            case ContractConstant.TX_TYPE_DELETE_CONTRACT :
                ContractDeleteInfo contractDeleteInfo = contractDeleteInfoMapper.selectBySearchable(searchable);
                transaction.setTxData(contractDeleteInfo);
                break;
            default:
        }

        Searchable searchable1 = new Searchable();
        searchable1.addCondition("tx_hash", SearchOperator.eq, hash);
        ContractResultInfo contractResultInfo = contractResultInfoMapper.selectBySearchable(searchable1);
        ContractTransactionDetailDto detail = new ContractTransactionDetailDto(transaction);
        detail.setResultDto(contractResultInfo);
        return detail;
    }
}