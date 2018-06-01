package io.nuls.api.server.business;

import io.nuls.api.entity.Alias;
import io.nuls.api.entity.Transaction;
import io.nuls.api.server.dao.mapper.AliasMapper;
import io.nuls.api.server.dao.mapper.TransactionMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 别名
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AliasBusiness {

    @Autowired
    private AliasMapper aliasMapper;

    /**
     * 根据地址获取别名
     * @param address 账户地址
     * @return
     */
    public Alias getAliasByAddress(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        return aliasMapper.selectBySearchable(searchable);
    }

    /**
     * 根据别名获取别名实体
     * @param alias 别名
     * @return
     */
    public Alias getAliasByAlias(String alias) {
        return aliasMapper.selectByPrimaryKey(alias);
    }

    /**
     * 验证该地址是否已经设置别名，并且别名是否没有重复然后设置别名
     * @param address 账户地址
     * @param alias 别名
     * @param height 区块高度
     * @return 0，设置失败，1设置成功，2已经设置过别名了，3别名已经被设置了
     */
    @Transactional
    public int insert(String address,String alias,Long height){
        if(getAliasByAddress(address)!=null){
            return 2;
        }
        if(getAliasByAlias(alias)!=null){
            return 3;
        }
        Alias entity = new Alias();
        entity.setAddress(address);
        entity.setAlias(alias);
        entity.setBlockHeight(height);
        return aliasMapper.insert(entity);
    }

    /**
     * 删除别名，根据账户地址删除
     * @param address 账户地址
     * @return
     */
    @Transactional
    public int deleteAliasById(String address){
        return aliasMapper.deleteByPrimaryKey(address);
    }

    /**
     * 删除别名，根据高度删除
     * @param height
     * @return
     */
    @Transactional
    public int deleteAliasByHeight(Long height){
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return aliasMapper.deleteBySearchable(searchable);
    }

}
