package io.nuls.api.server.business;

import io.nuls.api.context.AliasContext;
import io.nuls.api.entity.Alias;
import io.nuls.api.server.dao.mapper.AliasMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 别名
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AliasBusiness implements BaseService<Alias, String> {

    @Autowired
    private AliasMapper aliasMapper;

    /**
     * 根据地址获取别名
     *
     * @param address 账户地址
     * @return
     */
    public Alias  getAliasByAddress(String address) {
        Alias alias = AliasContext.get(address);
        if(null == alias){
            Searchable searchable = new Searchable();
            searchable.addCondition("address", SearchOperator.eq, address);
            alias = aliasMapper.selectBySearchable(searchable);
            if(null != alias){
                AliasContext.put(alias);
            }
        }
        return alias;
    }

    /**
     * 根据别名获取地址
     *
     * @param str 别名
     * @return
     */
    public Alias getAliasByAlias(String str) {
        Searchable searchable = new Searchable();
        searchable.addCondition("alias", SearchOperator.eq, str);
        return aliasMapper.selectBySearchable(searchable);
    }

    /**
     * 获取别名列表，全查询
     *
     * @return
     */
    public List<Alias> getList() {
        return aliasMapper.selectList(new Searchable());
    }

    /**
     * 验证该地址是否已经设置别名，并且别名是否没有重复然后设置别名
     *
     * @param alias 实体
     * @return 0，设置失败，1设置成功，2实体为空,，3地址格式验证失败，4高度错误,5别名为空，6账户地址已经设置过别名了，7别名已经被设置过了
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(Alias alias) {
        int result = aliasMapper.insert(alias);
        if (result == 1) {
            //保存成功后，同步别名到静态缓存中
            AliasContext.put(alias);
        }
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAll(List<Alias> list) {
        if (list.size() > 0) {
            int i = aliasMapper.insertByBatch(list);
            if(i > 0){
                AliasContext.putList(list);
            }
        }
        return 0;
    }

    /**
     * 别名不能修改
     * @param alias
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(Alias alias) {
        return aliasMapper.updateByPrimaryKey(alias);
    }

    /**
     * 别名不能删除
     *
     * @param id 账户地址
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(String id) {
        return aliasMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据主键获取别名
     *
     * @param id
     * @return
     */
    @Override
    public Alias getByKey(String id) {
        Alias alias = AliasContext.get(id);
        if(null == alias){
            alias = aliasMapper.selectByPrimaryKey(id);
            if(null != alias){
                //写入缓存
                AliasContext.put(alias);
            }
        }
        return alias;
    }


    /**
     * 删除别名，根据高度删除，只有回滚的时候才会调用，这时候，直接清空缓存
     *
     * @param height
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByHeight(long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        //回滚，直接清空别名缓存
        AliasContext.remove();
    }
}
