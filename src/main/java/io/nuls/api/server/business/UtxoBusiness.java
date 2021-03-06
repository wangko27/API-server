package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.dao.mapper.UtxoMapper;
import io.nuls.api.server.dao.mapper.leveldb.AddressHashIndexLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.UtxoLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.WebwalletUtxoLevelDbService;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.FreezeDto;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: UTXO
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Service
public class UtxoBusiness implements BaseService<Utxo, String> {
    @Autowired
    private UtxoMapper utxoMapper;
    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private WebwalletTransactionBusiness webwalletTransactionBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;

    private UtxoLevelDbService utxoLevelDbService = UtxoLevelDbService.getInstance();
    private WebwalletUtxoLevelDbService webwalletUtxoLevelDbService = WebwalletUtxoLevelDbService.getInstance();
    private AddressHashIndexLevelDbService addressHashIndexLevelDbService = AddressHashIndexLevelDbService.getInstance();

    /**
     * 获取列表 数据库查询
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
     * 通过leveldb查询全部，包含已花费，未花费
     *
     * @return
     */
    public List<Utxo> getList() {
        return utxoLevelDbService.getList();
    }

    /**
     * 根据地址，获取所有冻结的utxo
     *
     * @param address
     * @return
     */
    public PageInfo<FreezeDto> getListByAddress(String address, int pageNumber, int pageSize) {
        List<Utxo> utxoList = new ArrayList<>();
        Set<String> setList = UtxoContext.get(address);
        if(setList.size() == 0){
            return new PageInfo<>();
        }
        BlockHeader blockHeader = blockBusiness.getNewest();
        long bestHeight = 0L;
        if (blockHeader != null) {
            bestHeight = blockHeader.getHeight();
        }
        //还需要加上所有未确认交易的output中，状态为锁定的utxo
        List<WebwalletTransaction> webwalletTransactionList = webwalletTransactionBusiness.getAll(address, EntityConstant.WEBWALLET_STATUS_NOTCONFIRM,0);
        for(WebwalletTransaction tx: webwalletTransactionList){
            if(null != tx.getOutputs()){
                for(Utxo utxo:tx.getOutputs()){
                    if(utxo.getAddress().equals(address) && utxo.getLockTime()==-1){
                        utxoList.add(utxo);
                    }
                }
            }
        }
        List<FreezeDto> tempList = new ArrayList<>();
        for (String str : setList) {
            Utxo utxo = utxoLevelDbService.select(str);
            if(!utxo.usable(bestHeight)){
                Transaction transaction = transactionBusiness.getByHash(utxo.getTxHash());
                FreezeDto freezeDto = new FreezeDto(transaction.getCreateTime(),utxo.getLockTime(),transaction.getHash(),transaction.getType(),utxo.getAmount());
                tempList.add(freezeDto);
            }
        }
        Collections.sort(tempList, new Comparator<FreezeDto>() {
            @Override
            public int compare(FreezeDto o1, FreezeDto o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });

        //模拟分页
        PageInfo<FreezeDto> page = new PageInfo<>();
        int start = (pageNumber - 1) * pageSize;
        if(start > tempList.size()){
            return page;
        }
        int end = start + pageSize;
        if(end > tempList.size()){
            end = tempList.size();
        }
        List<FreezeDto> freezeDtoList = new ArrayList<>();
        for(int i = start;i<end;i++){
            freezeDtoList.add(tempList.get(i));
        }
        page.setList(freezeDtoList);
        page.setTotal(tempList.size());
        page.setPageNum(pageNumber);
        page.setSize(pageSize);
        return page;
    }

    /**
     * 查询账户可用的utxo
     * @param address 用户账户
     * @return
     */
    public List<Utxo> getUsableUtxo(String address) {
        List<Utxo> list = new ArrayList<>();
        BlockHeader blockHeader = blockBusiness.getNewest();
        long bestHeight = 0L;
        if (blockHeader != null) {
            bestHeight = blockHeader.getHeight();
        }
        if(bestHeight < 0){
            return list;
        }
        Set<String> keyList = UtxoContext.get(address);
        if(keyList.size() == 0){
            return list;
        }
        List<Utxo> utxoList = utxoLevelDbService.selectList(keyList);
        //还需要过滤数据库中所有未确认交易的input列表中的utxo
        List<WebwalletTransaction> webwalletTransactionList = webwalletTransactionBusiness.getAll(address, EntityConstant.WEBWALLET_STATUS_NOTCONFIRM,0);
        Set<String> lockedUtxo = new HashSet<>();
        for(WebwalletTransaction tx: webwalletTransactionList){
            if(null != tx.getInputs()){
                for(Input input:tx.getInputs()){
                    lockedUtxo.add(input.getKey());
                }
            }
        }
        //把上一次花费剩下的可用utxo加入到可用里面
        Utxo temp = webwalletUtxoLevelDbService.select(address);
        if(null != temp){
            list.add(temp);
            for (Utxo utxo : utxoList) {
                if(utxo.getKey().equals(temp.getKey())){
                    //删除转账时临时产生的utxo
                    webwalletUtxoLevelDbService.delete(utxo.getAddress());
                    list.remove(temp);
                    break;
                }
            }
        }
        for (Utxo utxo : utxoList) {
            if(lockedUtxo.contains(utxo.getKey())){
                continue;
            }
            if(utxo.usable(bestHeight)){
                list.add(utxo);
            }
        }
        //排序
        Collections.sort(list, new Comparator<Utxo>() {
            @Override
            public int compare(Utxo o1, Utxo o2) {
                return o1.getAmount().compareTo(o2.getAmount());
            }
        });
        return list;
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
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(Utxo entity) {
        return utxoLevelDbService.insert(entity);
        //return utxoMapper.insert(entity);
    }


    @Override
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
    public void rollBackByFrom(Map<String, Utxo> inputMap) {
        utxoLevelDbService.insertMap(inputMap);
        for (Utxo utxo : inputMap.values()) {
            UtxoContext.put(utxo.getAddress(), utxo.getKey());
        }
    }

    public List<Utxo> getListByFrom(Transaction tx, Map<String, Utxo> utxoMap) {
        //coinBase交易，红黄牌交易没有inputs
        List<Utxo> utxoList = new ArrayList<>();
        if (tx.getInputs() == null || tx.getInputs().isEmpty()) {
            return utxoList;
        }
        Utxo utxo;
        String key;
        for (Input input : tx.getInputs()) {
            //重置需要添加的数据的spend_hash
            key = input.getKey();
            if (utxoMap.containsKey(key)) {
                utxo = utxoMap.get(key);
                utxo.setSpendTxHash(tx.getHash());
                input.setAddress(utxo.getAddress());
                input.setValue(utxo.getAmount());
                continue;
            }

            utxo = utxoLevelDbService.select(key);
            if (null != utxo) {
                utxo.setSpendTxHash(tx.getHash());
                input.setAddress(utxo.getAddress());
                input.setValue(utxo.getAmount());
                utxoList.add(utxo);
            }
        }
        return utxoList;
    }

    public void rollbackByTo(List<Utxo> outputs) {
        Map<String,AddressHashIndex> attrMapList = new HashMap<>();
        for (Utxo utxoKey : outputs) {
            AddressHashIndex addressHashIndex = null;
            utxoLevelDbService.delete(utxoKey.getKey());
            if(attrMapList.containsKey(utxoKey.getAddress())){
                //已经存在，直接移除
                attrMapList.get(utxoKey.getAddress()).getHashIndexSet().remove(utxoKey.getKey());
            }else{
                //不存在，新建一个，去leveldb获取数据，然后删除
                addressHashIndex = new AddressHashIndex();
                addressHashIndex.setAddress(utxoKey.getAddress());
                Set<String> setList = UtxoContext.get(utxoKey.getAddress());
                setList.remove(utxoKey.getKey());
                addressHashIndex.setHashIndexSet(setList);
                attrMapList.put(utxoKey.getAddress(),addressHashIndex);
            }
        }
        //重置缓存和leveldb
        UtxoContext.putMap(attrMapList);
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
    /*public List<UtxoDto> getBlockSumTxamount() {
        Map<String, UtxoDto> mapData = new HashMap<>();
        List<Utxo> utxoList = getList();
        UtxoDto utxoDto;
        for(Utxo utxo : utxoList) {
            if (null == utxo.getSpendTxHash()) {
                if (mapData.containsKey(utxo.getAddress())) {
                    utxoDto = mapData.get(utxo.getAddress());
                    mapData.get(utxo.getAddress()).setTotal(utxoDto.getTotal() + utxo.getAmount());
                } else {
                    utxoDto = new UtxoDto();
                    utxoDto.setAddress(utxo.getAddress());
                    utxoDto.setTotal(utxo.getAmount());
                    mapData.put(utxo.getAddress(), utxoDto);
                }
            }
        }
        return new ArrayList<UtxoDto>(mapData.values());
    }*/


    /**
     * 统计持币账户 扫描所有utxo   更新于2018-09-19，采用单账户统计余额的方式统计，对于utxo越多，效率相对会更高
     *
     * @return
     */
    public List<UtxoDto> getBlockSumTxamount(){
        List<UtxoDto> ulist = new ArrayList<>();
        List<AddressHashIndex> allList = addressHashIndexLevelDbService.getAll();
        Utxo utxo;
        UtxoDto utxoDto;
        for(AddressHashIndex addressHashIndex : allList){
            Set<String> utxoSet = addressHashIndex.getHashIndexSet();
            utxoDto = new UtxoDto();
            utxoDto.setAddress(addressHashIndex.getAddress());
            for(String hashIndex:utxoSet){
                utxo = selectUtxoByHashAndIndex(hashIndex);
                if(null == utxo.getSpendTxHash()){
                    utxoDto.setTotal(utxo.getAmount()+utxoDto.getTotal());
                }
            }
            if(utxoDto.getTotal() > 0){
                ulist.add(utxoDto);
            }
        }
        return ulist;
    }
}
