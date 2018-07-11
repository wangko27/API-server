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
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
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

    private UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();

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

    /**
     * 查询全部，包含已花费，未花费
     *
     * @return
     */
    public List<Utxo> getList() {
        List<Utxo> list = utxoLevelDbService.getList();
        return list;
    }

    /**
     * 查询全部，未花费
     *
     * @return
     */
    public List<Utxo> getUtxoList() {
        List<Utxo> utxoList = new ArrayList<>();
        List<Utxo> list = utxoLevelDbService.getList();
        if(null == list){
            return utxoList;
        }
        for (Utxo utxo : list) {
            if (StringUtils.isBlank(utxo.getSpendTxHash())) {
                utxoList.add(utxo);
            }
        }
        return utxoList;
    }

    /**
     * init Utxo
     *
     * @return
     */
    public void initUtxoList() {
        List<Utxo> list = getUtxoList();
        for (Utxo utxo : list) {
            UtxoContext.put(utxo.getAddress(), utxo.getKey());
        }
    }

    private Utxo selectUtxoByHashAndIndex(String hashIndex) {
        return utxoLevelDbService.select(hashIndex);
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
        Utxo utxo = selectUtxoByHashAndIndex(hash + "_" + index);
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
        return selectUtxoByHashAndIndex(txHash + "_" + txIndex);
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
        if (list.size() > 0) {
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
        String key = txHash + "_" + index;
        return deleteByKey(key);
    }

    //根据交易获取要修改的utxo，之后执行批量修改
    public List<Utxo> getListByFrom(Transaction tx, Map<String, Utxo> utxoMap) {
        //coinBase交易，红黄牌交易没有inputs
        List<Utxo> txlist = new ArrayList<>();
        if (tx.getInputs() == null || tx.getInputs().isEmpty()) {
            return txlist;
        }
        Utxo utxo;
        String key;
        for (Input input : tx.getInputs()) {
            //重置需要添加的数据的spend_hash
            key = input.getKey();
            if (utxoMap.containsKey(key)) {
                utxoMap.get(key).setSpendTxHash(tx.getHash());
                continue;
            }

            utxo = utxoLevelDbService.select(key);
            if (null != utxo) {
                utxo.setSpendTxHash(tx.getHash());
                input.setAddress(utxo.getAddress());
                input.setValue(utxo.getAmount());
                txlist.add(utxo);
            }
        }
        return txlist;
    }

    public void rollBackByFrom(Map<String, Utxo> inputMap) {
        utxoLevelDbService.insertMap(inputMap);
        for (Utxo utxo : inputMap.values()) {
            UtxoContext.put(utxo.getAddress(), utxo.getKey());
        }
    }

    public void rollbackByTo(List<Utxo> outputs) {
        for (Utxo utxo : outputs) {
            utxoLevelDbService.delete(utxo.getKey());
            UtxoContext.remove(utxo.getAddress(), utxo.getKey());
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(Map<String, Utxo> utxoMap) {
        utxoLevelDbService.insertMap(utxoMap);
    }

    /**
     * 统计持币账户 扫描所有utxo
     *
     * @return
     */
    public List<UtxoDto> getBlockSumTxamount() {
        Map<String,UtxoDto> mapData = new HashMap<>();
        List<Utxo> utxoList = utxoLevelDbService.getList();
        UtxoDto utxoDto;
        for (Utxo utxo : utxoList) {
            if(null == utxo.getSpendTxHash()){
                if(mapData.containsKey(utxo.getAddress())){
                    utxoDto = mapData.get(utxo.getAddress());
                    mapData.get(utxo.getAddress()).setTotal(utxoDto.getTotal()+utxo.getAmount());
                }else{
                    utxoDto = new UtxoDto();
                    utxoDto.setAddress(utxo.getAddress());
                    utxoDto.setTotal(utxo.getAmount());
                    mapData.put(utxo.getAddress(),utxoDto);
                }
            }
        }
        return new ArrayList<UtxoDto>(mapData.values());
    }
}
