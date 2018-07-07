package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.context.UtxoTempContext;
import io.nuls.api.entity.Input;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.UtxoMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.utils.ArraysTool;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: UTXO
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class UtxoBusiness implements BaseService<Utxo, Long> {
    @Autowired
    private UtxoMapper utxoMapper;

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
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        return utxoMapper.selectList(searchable);
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

    private Utxo selectUtxoByHashAndIndex(String hash, Integer index){
        Utxo utxo = UtxoTempContext.get(hash+index);
        if(utxo == null){
            Searchable searchable = new Searchable();
            searchable.addCondition("tx_hash", SearchOperator.eq, hash);
            searchable.addCondition("tx_index", SearchOperator.eq, index);
            utxo = utxoMapper.selectByHashAndIndex(searchable);
        }
        return utxo;
    }
    private int deleteByHashAndIndex(String hash, Integer index){
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, hash);
        searchable.addCondition("tx_index", SearchOperator.eq, index);
        return utxoMapper.deleteByHashAndIndex(searchable);
    }

    /**
     * 根据hash和id查询已经花费的utxo的详情
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

        Utxo utxo = selectUtxoByHashAndIndex(hash,index);
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
        return selectUtxoByHashAndIndex(txHash,txIndex);
    }

    /**
     * 新增
     *
     * @param entity
     * @return 1操作成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(Utxo entity) {
        return utxoMapper.insert(entity);
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(Utxo entity) {
        return utxoMapper.updateByPrimaryKey(entity);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateAll(List<Utxo> list) {
        if(list.size() > 0){
            if(list.size()>4000){
                List<List<Utxo>> lists = ArraysTool.avgList(list,2);
                utxoMapper.updateByBatch(lists.get(0));
                utxoMapper.updateByBatch(lists.get(1));
            }else{
                return utxoMapper.updateByBatch(list);
            }
        }
        return 0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(Long id) {
        return utxoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Utxo getByKey(Long id) {
        return utxoMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据主键删除
     *
     * @param txHash
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete(String txHash, Integer index) {
        return deleteByHashAndIndex(txHash,index);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByTxHash(String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, txHash);
        utxoMapper.deleteBySearchable(searchable);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteBySpendHash() {
        Searchable searchable = new Searchable();
        searchable.addCondition("spend_tx_hash", SearchOperator.isNotNull, null);
        utxoMapper.deleteBySearchable(searchable);
    }

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
            utxo = selectUtxoByHashAndIndex(input.getFromHash(),input.getFromIndex());
            if(null != utxo){
                utxo.setSpendTxHash(tx.getHash());
                input.setAddress(utxo.getAddress());
                input.setValue(utxo.getAmount());
                txlist.add(utxo);
                //删除缓存
                UtxoContext.remove(utxo.getAddress());
                UtxoTempContext.remove(utxo.getSpendTxHash()+utxo.getTxIndex());
            }
        }
        return txlist;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollBackByFrom(Transaction tx) {
        Searchable searchable = new Searchable();
        searchable.addCondition("tx_hash", SearchOperator.eq, tx.getHash());
        utxoMapper.deleteBySearchable(searchable);

        //回滚每条被花费的输出
        //UtxoKey utxoKey;
        for (Input input : tx.getInputs()) {
            //utxoKey = new UtxoKey(input.getFromHash(), input.getFromIndex());
            //System.out.println("3kd");
            Utxo utxo = selectUtxoByHashAndIndex(input.getFromHash(),input.getFromIndex());
            utxo.setSpendTxHash(null);
            utxoMapper.updateByPrimaryKey(utxo);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<Utxo> list){

        if(list.size()>0){
            if(list.size()>1000) {
                int count = list.size()%1000;
                List<List<Utxo>> lists = ArraysTool.avgList(list, count);
                for(int i = 0; i<count; i++){
                    utxoMapper.insertByBatch(lists.get(i));
                }

            }else{
                utxoMapper.insertByBatch(list);
            }
        }
    }

    /**
     * 统计持币账户
     *
     * @return
     */
    public List<UtxoDto> getBlockSumTxamount() {
        return utxoMapper.getBlockSumTxamount();
    }
}
