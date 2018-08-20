package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int save(WebwalletTransaction webwalletTransaction) {
        if(webwalletTransactionMapper.insert(webwalletTransaction) == 1){
            return webwalletTransactionLevelDbService.insert(webwalletTransaction);
        }
        /*if(i == 1){
            //保存leveldb
            List<Utxo> utxoList = webwalletTransaction.getOutputs();
            //如果有输出，则直接放入 这里只有四种类型，转账、设置别名、委托、退出委托
            for(Utxo utxo : utxoList){
                if(webwalletTransaction.getAddress().equals(utxo.getAddress()) && utxo.getLockTime() == 0){
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
        }*/
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
            //保存leveldb
            List<Utxo> utxoList = webwalletTransaction.getOutputs();
            //如果有输出，则直接放入 这里只有四种类型，转账、设置别名、委托、退出委托
            for(Utxo utxo : utxoList){
                if(webwalletTransaction.getAddress().equals(utxo.getAddress()) && utxo.getLockTime() == 0){
                    webwalletUtxoLevelDbService.insert(utxo);
                }
            }
            return webwalletTransactionLevelDbService.insert(webwalletTransaction);
        }
        return 0;
    }

    /**
     * 交易确认后，删除leveldb的缓存和数据库的数据
     * @param list
     */
    public void deleteStatusByList(List<Transaction> list){
        if(null != list){
            for(Transaction tx:list){
                //webwalletTransactionMapper.updateStatusByPrimaryKey(tx.getHash());
                webwalletTransactionMapper.deleteByPrimaryKey(tx.getHash());
                webwalletTransactionLevelDbService.delete(tx.getHash());
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
        PageHelper.orderBy("time asc");
        return formatWebwalletTransaction(webwalletTransactionMapper.selectList(searchable));
    }

    /**
     * 查询十分钟前的未确认交易
     * @param status
     * @param endTime
     * @return
     */
    public List<WebwalletTransaction> getListByTime(int status,long endTime){
        Searchable searchable = new Searchable();
        if(status > 0){
            searchable.addCondition("status", SearchOperator.eq, status);
        }
        if(endTime > 0){
            searchable.addCondition("time", SearchOperator.lte, endTime);
        }
        PageHelper.orderBy("time asc");
        return formatWebwalletTransaction(webwalletTransactionMapper.selectList(searchable));
    }

    private List<WebwalletTransaction> formatWebwalletTransaction(List<WebwalletTransaction> list){
        List<WebwalletTransaction> dataList = new ArrayList<>();
        if(null != list && list.size() > 0){
            for(WebwalletTransaction tx: list){
                dataList.add(getByKey(tx.getHash()));
            }
        }
        return dataList;
    }
}
