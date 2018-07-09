package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.UtxoMapper;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.utils.ArraysTool;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: UTXO
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class UtxoBusiness implements BaseService<Utxo, String> {
    @Autowired
    private UtxoMapper utxoMapper;
    UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();

    /**
     * 获取列表
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<Utxo> getList(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        PageInfo<Utxo> page = new PageInfo<>(utxoMapper.selectList(searchable));
        return page;
    }

    public List<Utxo> getList(String txHash) {
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(txHash)){
            //todo 待验证 正确性
            searchable.addCondition("tx_hash", SearchOperator.suffixLike, txHash);
        }
        return utxoMapper.selectList(searchable);
    }

    public int deleteByTxHash(String txHash){
        //todo 根据hash删除utxo
        return 0;
    }

    /**
     * 根据地址获取该地址全部的utxo
     *
     * @param address
     * @param type    1查询全部 2查询未花费 3查询已花费
     * @return
     */
    public List<Utxo> getList(String address, int type) {

        Searchable searchable = new Searchable();
        if (StringUtils.validAddress(address)) {
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        if (type > 0) {
            if (type == 2) {
                searchable.addCondition("spend_tx_hash", SearchOperator.isNull, null);
            } else if (type == 3) {
                searchable.addCondition("spend_tx_hash", SearchOperator.isNotNull, null);
            }
        }
        return utxoMapper.selectList(searchable);
    }

    private Utxo selectUtxoByHashAndIndex(String hashIndex){
        /*Utxo utxo = UtxoTempContext.get(hashIndex);
        if(utxo == null){
            *//*Searchable searchable = new Searchable();
            searchable.addCondition("tx_hash", SearchOperator.eq, hash);
            searchable.addCondition("tx_index", SearchOperator.eq, index);
            utxo = utxoMapper.selectByHashAndIndex(searchable);*//*
            //去leveldb获取
            utxo = utxoLevelDbService.select(hashIndex);
        }*/
        return utxoLevelDbService.select(hashIndex);
    }
    private int deleteByHashAndIndex(String hash, Integer index){
        /*Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, hash);
        searchable.addCondition("tx_index", SearchOperator.eq, index);
        return utxoMapper.deleteByHashAndIndex(searchable);*/
        //leveldb 删除
        return utxoLevelDbService.delete(hash+"_"+index);
    }

    /**
     * 根据hash和id查询已经花费的utxo的详情     ---已废弃，待重新开发
     *
     * @param hash
     * @param index
     * @return
     */
    public String getUtxoBySpentHash(String hash, Integer index) {
        if (!StringUtils.validHash(hash)) {
            return null;
        }
        if (index < 0) {
            return null;
        }

        Utxo utxo = selectUtxoByHashAndIndex(hash+"_"+index);
        if (null != utxo) {
            return utxo.getSpendTxHash();
        }
        return null;
    }

    /**
     * 根据主键获取详情
     *
     * @param txHash
     * @param txIndex
     * @return
     */
    public Utxo getByKey(String txHash, int txIndex) {

        return selectUtxoByHashAndIndex(txHash+"_"+txIndex);
    }

    /**
     * 新增
     *
     * @param entity
     * @return 1操作成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(Utxo entity) {
        return utxoLevelDbService.insert(entity);
        //return utxoMapper.insert(entity);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(Utxo entity) {
        return utxoLevelDbService.insert(entity);
        //return utxoMapper.updateByPrimaryKey(entity);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateAll(List<Utxo> list) {
        if(list.size() > 0){
            /*if(list.size()>4000){
                List<List<Utxo>> lists = ArraysTool.avgList(list,2);
                utxoMapper.updateByBatch(lists.get(0));
                utxoMapper.updateByBatch(lists.get(1));
            }else{
                return utxoMapper.updateByBatch(list);
            }*/
            utxoLevelDbService.insertList(list);
        }
        return 0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(String hashIndex) {
        return utxoLevelDbService.delete(hashIndex);
        //return utxoMapper.deleteByPrimaryKey(hashIndex);
    }

    @Override
    public Utxo getByKey(String hashIndex) {
        return utxoLevelDbService.select(hashIndex);
        //return utxoMapper.selectByPrimaryKey(hashIndex);
    }

    /**
     * 根据主键删除
     *
     * @param txHash
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete(String txHash, Integer index) {
        String key = txHash+"_"+index;
        return deleteByKey(key);
        //return utxoLevelDbService.delete(key);
    }

    //todo 根据tx_hash删除utxo
    /*@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByTxHash(String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        utxoMapper.deleteBySearchable(searchable);
    }*/

    //根据交易获取要修改的utxo，之后执行批量修改
    public List<Utxo> getListByFrom(Transaction tx,Map<String,Utxo> utxoMap) {
        //coinBase交易，红黄牌交易没有inputs
        List<Utxo> txlist = new ArrayList<>();
        if (tx.getInputs() == null) {
            return txlist;
        }
        Utxo utxo;
        for (Input input : tx.getInputs()) {
            //重置需要添加的数据的spend_hash
            String key = input.getFromHash()+input.getFromIndex();
            if(utxoMap.containsKey(key)){
                utxoMap.get(key).setSpendTxHash(tx.getHash());
                continue;
            }
            utxo = selectUtxoByHashAndIndex(key);
            if(null != utxo){
                utxo.setSpendTxHash(tx.getHash());
                input.setAddress(utxo.getAddress());
                input.setValue(utxo.getAmount());
                txlist.add(utxo);
                //删除缓存
                UtxoContext.remove(utxo.getAddress());
            }
        }
        return txlist;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollBackByFrom(Transaction tx) {
        /*Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, tx.getHash());
        utxoMapper.deleteBySearchable(searchable);*/
        //todo 根据tx_hash回滚utxo，先从数据库查询出需要回滚的tx_hash，然后根据这些utxo去leveldb删除

        //回滚每条被花费的输出
        //UtxoKey utxoKey;
        for (Input input : tx.getInputs()) {
            //utxoKey = new UtxoKey(input.getFromHash(), input.getFromIndex());
            //System.out.println("3kd");
            Utxo utxo = selectUtxoByHashAndIndex(input.getFromHash()+"_"+input.getFromIndex());
            utxo.setSpendTxHash(null);
            //utxoMapper.updateByPrimaryKey(utxo);
            utxoLevelDbService.insert(utxo);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<Utxo> list){

        if(list.size()>0){
            /*if(list.size()>1000) {
                //执行批量插入
                int count = list.size()%1000;
                List<List<Utxo>> lists = ArraysTool.avgList(list, count);
                for(int i = 0; i<count; i++){
                    utxoMapper.insertByBatch(lists.get(i));
                }
                //LOAD DATA LOCAL INFILE

            }else{
                utxoMapper.insertByBatch(list);
            }*/
            //存入leveldb
            utxoLevelDbService.insertList(list);

        }
    }

    /**
     * 统计持币账户
     *
     * @return
     */
    //todo 统计持币账户
    public List<UtxoDto> getBlockSumTxamount() {
        return new ArrayList<>();
        //return utxoMapper.getBlockSumTxamount();
    }
}
