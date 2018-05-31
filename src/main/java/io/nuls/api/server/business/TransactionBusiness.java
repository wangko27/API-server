package io.nuls.api.server.business;

import io.nuls.api.entity.Transaction;
import io.nuls.api.server.dao.mapper.TransactionMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 交易
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class TransactionBusiness {

    @Autowired
    private TransactionMapper transactionMapper;

    /**
     * 交易列表
     * @param address 所属的出块地址
     * @param height  所属的区块
     * @param type 交易类型
     * @return
     */
    public List<Transaction> getList(String address,Long height,int type) {
        Searchable searchable = new Searchable();
        /*if(StringUtils.isNotBlank(address)){
            searchable.addCondition("address", SearchOperator.eq, address);
        }*/
        if(height>=0){
            searchable.addCondition("block_height", SearchOperator.eq, height);
        }
        if(height>=0){
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        return transactionMapper.selectList(searchable);
    }

    /**
     * 根据交易hash查交易详情
     * @param hash 交易hash
     * @return
     */
    public Transaction getTransactionDetail(String hash) {
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(hash)){
            searchable.addCondition("hash", SearchOperator.eq, hash);
        }
        return transactionMapper.selectBySearchable(searchable);
    }

}
