package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.context.UtxoContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.dao.mapper.WebwalletTransactionMapper;
import io.nuls.api.server.dao.mapper.leveldb.WebwalletTransactionLevelDbService;
import io.nuls.api.server.dao.mapper.leveldb.WebwalletUtxoLevelDbService;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:
 * Author: zsj
 * Date:  2018/7/30 0030
 */
@Service
public class WebwalletTransactionBusiness implements BaseService<WebwalletTransaction,String> {

    @Autowired
    private WebwalletTransactionMapper webwalletTransactionMapper;
    @Autowired
    private UtxoBusiness utxoBusiness;

    private WebwalletTransactionLevelDbService webwalletTransactionLevelDbService = WebwalletTransactionLevelDbService.getInstance();
    private WebwalletUtxoLevelDbService webwalletUtxoLevelDbService = WebwalletUtxoLevelDbService.getInstance();

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(WebwalletTransaction webwalletTransaction,String address) {
        int i = webwalletTransactionMapper.insert(webwalletTransaction);
        if(i == 1){
            //保存leveldb
            List<Utxo> utxoList = webwalletTransaction.getOutputs();
            List<Input> inputList = webwalletTransaction.getInputs();
            Map<String,AddressHashIndex> attrMapList = new HashMap<>();
            for (Input input: inputList) {
                AddressHashIndex addressHashIndex = null;
                Utxo utxoKey = utxoBusiness.getByKey(input.getKey());
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
            //修改待确认交易的状态
            for(Utxo utxo : utxoList){
                if(address.equals(utxo.getAddress())){
                    webwalletUtxoLevelDbService.insert(utxo);
                }
            }
            return webwalletTransactionLevelDbService.insert(webwalletTransaction);
        }else{
            try {
                throw new Exception("保存失败");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(WebwalletTransaction webwalletTransaction) {
        return 0;
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteConfirmTx() {
        return webwalletTransactionMapper.deleteConfirmTx();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int update(WebwalletTransaction webwalletTransaction) {
        int i = webwalletTransactionMapper.updateByPrimaryKey(webwalletTransaction);
        if(i == 1){
            return webwalletTransactionLevelDbService.insert(webwalletTransaction);
        }
        return 0;
    }

    public void updateStatusByList(List<Transaction> list){
        if(null != list){
            for(Transaction tx:list){
                webwalletTransactionMapper.updateStatusByPrimaryKey(tx.getHash());
            }
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(String s) {
        int i = webwalletTransactionMapper.deleteByPrimaryKey(s);
        if(i == 1){
            return webwalletTransactionLevelDbService.delete(s);
        }
        return 0;
    }

    @Override
    public WebwalletTransaction getByKey(String s) {
        return webwalletTransactionLevelDbService.select(s);
    }

    public PageInfo<WebwalletTransaction> getAll(String address,int status,int type,int pageNumber,int pageSize){
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        if(status > 0){
            searchable.addCondition("status", SearchOperator.eq, status);
        }
        if(type > 0){
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        PageHelper.orderBy("time desc");
        PageInfo<WebwalletTransaction> page = new PageInfo<>(webwalletTransactionMapper.selectList(searchable));
        List<WebwalletTransaction> webwalletTransactionList = formatWebwalletTransaction(page.getList());
        if (StringUtils.isNotBlank(address)) {
            for(WebwalletTransaction wtx: webwalletTransactionList){
                wtx.caclTx(wtx,address);
                wtx.setSignData(null);
            }
        }
        page.setList(webwalletTransactionList);
        return page;
    }

    public List<WebwalletTransaction> getAll(String address,int status,int type){
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(address)) {
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        if(status > 0){
            searchable.addCondition("status", SearchOperator.eq, status);
        }
        if(type > 0){
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        return formatWebwalletTransaction(webwalletTransactionMapper.selectList(searchable));
    }

    private List<WebwalletTransaction> formatWebwalletTransaction(List<WebwalletTransaction> list){
        List<WebwalletTransaction> dataList = new ArrayList<>();
        if(null != list && list.size() > 0){
            for(WebwalletTransaction tx: list){
                dataList.add(getByKey(tx.getAddress()));
            }
        }
        return dataList;
    }
}
