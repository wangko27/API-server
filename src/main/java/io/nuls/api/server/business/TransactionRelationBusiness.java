package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.TransactionRelation;

import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.TransactionRelationMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.ArraysTool;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionRelationBusiness implements BaseService<TransactionRelation, Long> {

    @Autowired
    private TransactionRelationMapper relationMapper;

    /**
     * 根据地址查询全部
     *
     * @param address
     * @return
     */
    public List<TransactionRelation> getList(String address) {
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        return relationMapper.selectList(searchable);
    }

    /**
     * 判断某个地址是否有交易存在
     * @param address
     * @return
     */
    public long isAddressExist(String address){
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        return relationMapper.isAddressExist(searchable);
    }

    /**
     * 根据地址，分页查询hash
     * @param address
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<TransactionRelation> getListByPage(String address,int type,long startTime,long endTime,int pageNumber,int pageSize) {

        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        if(type > 0){
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        //前端已改
        /*if(startTime==endTime && startTime > 0){
            //特殊情况，前段传过来的两个一样的日期，都是当天的00:00:00，就查询这一天的交易即可
            searchable.addCondition("create_time", SearchOperator.gte, startTime);
            searchable.addCondition("create_time", SearchOperator.lte, startTime+ Constant.MILLISECONDS_TIME_DAY);
        }else{

        }*/
        if(startTime > 0){
            searchable.addCondition("create_time", SearchOperator.gte, startTime);
        }
        if(endTime > 0){
            searchable.addCondition("create_time", SearchOperator.lte, endTime);
        }
        PageHelper.orderBy("id desc");
        PageInfo<TransactionRelation> page = new PageInfo<>(relationMapper.selectList(searchable));
        return page;
    }

    public List<TransactionRelation> getListByTx(Transaction tx) {
        Set<String> addressSet = new HashSet<>();
        if (tx.getInputs() != null && !tx.getInputs().isEmpty()) {
            for (Input input : tx.getInputs()) {
                if (StringUtils.isNotBlank(input.getAddress())) {
                    addressSet.add(input.getAddress());
                }
            }
        }
        if (tx.getOutputs() != null && !tx.getOutputs().isEmpty()) {
            for (Utxo utxo : tx.getOutputs()) {
                if (StringUtils.isNotBlank(utxo.getAddress())) {
                    addressSet.add(utxo.getAddress());
                }
            }
        }
        List<TransactionRelation> relationList = new ArrayList<>();
        for (String address : addressSet) {
            TransactionRelation key = new TransactionRelation(address, tx.getHash(),tx.getType(),tx.getCreateTime());
            relationList.add(key);
        }
        return relationList;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<TransactionRelation> list) {
        /*for(TransactionRelation txr:relationList){
            relationMapper.insert(txr);
        }*/
        if (list.size() > 0) {
            if (list.size() > 1000) {
                int count = list.size() % 1000;
                List<List<TransactionRelation>> lists = ArraysTool.avgList(list, count);
                for (int i = 0; i < count; i++) {
                    relationMapper.insertByBatch(lists.get(i));
                }
            } else {
                relationMapper.insertByBatch(list);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByTxHash(String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        relationMapper.deleteByTxHash(txHash);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteList(List<String> txHashList) {
        if(null != txHashList){
            relationMapper.deleteList(txHashList);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(TransactionRelation transactionRelation) {
        return relationMapper.insert(transactionRelation);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(TransactionRelation transactionRelation) {
        return relationMapper.updateByPrimaryKey(transactionRelation);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(Long aLong) {
        return relationMapper.deleteByPrimaryKey(aLong);
    }

    @Override
    public TransactionRelation getByKey(Long aLong) {
        return relationMapper.selectByPrimaryKey(aLong);
    }
}
