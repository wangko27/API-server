package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.Utxo;
import io.nuls.api.entity.UtxoKey;
import io.nuls.api.server.dao.mapper.UtxoMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: UTXO
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class UtxoBusiness {
    @Autowired
    private UtxoMapper utxoMapper;

    /**
     * 获取列表
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<Utxo> getList(String address,int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(address)){
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        PageInfo<Utxo> page = new PageInfo<>(utxoMapper.selectList(searchable));
        return page;
    }

    /**
     * 根据主键获取详情
     * @param txHash
     * @param txIndex
     * @return
     */
    public Utxo getDetail(String txHash,int txIndex){
        UtxoKey key = new UtxoKey();
        key.setTxHash(txHash);
        key.setTxIndex(txIndex);
        return utxoMapper.selectByPrimaryKey(key);
    }

    /**
     * 新增
     * @param entity
     * @return 1操作成功，其他失败
     */
    @Transactional
    public int insert(Utxo entity){
        return utxoMapper.insert(entity);
    }

    /**
     * 根据主键删除
     * @param key
     * @return
     */
    @Transactional
    public int deleteById(UtxoKey key){
        return utxoMapper.deleteByPrimaryKey(key);
    }

}
