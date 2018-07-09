package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
     * @param address
     * @return
     */
    public List<TransactionRelation> getList(String address){
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(address)){
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        return relationMapper.selectList(searchable);
    }

    /**
     * 根据地址，分页查询hash
     * @param address
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<TransactionRelation> getListByPage(String address,int pageNumber, int pageSize){
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(address)){
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        PageHelper.orderBy("id desc");
        PageInfo<TransactionRelation> page = new PageInfo<>(relationMapper.selectList(searchable));
        return page;
    }

    public List<TransactionRelation> getListByTx(Transaction tx){
        Set<String> addressSet = new HashSet<>();
        if (tx.getInputs() != null) {
            for (Input input : tx.getInputs()) {
                if(StringUtils.isNotBlank(input.getAddress())){
                    addressSet.add(input.getAddress());
                }

            }
        }
        if (tx.getOutputs() != null) {
            for (Utxo utxo : tx.getOutputs()) {
                if(StringUtils.isNotBlank(utxo.getAddress())) {
                    addressSet.add(utxo.getAddress());
                }
            }
        }
        List<TransactionRelation> relationList = new ArrayList<>();
        for (String address : addressSet) {
            TransactionRelation key = new TransactionRelation(address, tx.getHash());
            relationList.add(key);
        }
        return relationList;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<TransactionRelation> list ) {
        /*for(TransactionRelation txr:relationList){
            relationMapper.insert(txr);
        }*/
        if(list.size() > 0){
            if(list.size()>1000) {
                int count = list.size()%1000;
                List<List<TransactionRelation>> lists = ArraysTool.avgList(list, count);
                for(int i = 0; i<count; i++){
                    relationMapper.insertByBatch(lists.get(i));
                }
            }else{
                relationMapper.insertByBatch(list);
            }
        }
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByTxHash(String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        relationMapper.deleteByTxHash(txHash);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(TransactionRelation transactionRelation) {
        return relationMapper.insert(transactionRelation);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(TransactionRelation transactionRelation) {
        return relationMapper.updateByPrimaryKey(transactionRelation);
    }

    @Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(Long aLong) {
        return relationMapper.deleteByPrimaryKey(aLong);
    }

    @Override
    public TransactionRelation getByKey(Long aLong) {
        return relationMapper.selectByPrimaryKey(aLong);
    }
}
