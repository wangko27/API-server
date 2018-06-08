package io.nuls.api.server.business;

import io.nuls.api.context.AliasContext;
import io.nuls.api.entity.Alias;
import io.nuls.api.server.dao.mapper.AliasMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
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
public class AliasBusiness implements BaseService<Alias, String> {

    @Autowired
    private AliasMapper aliasMapper;

    /**
     * 根据地址获取别名
     *
     * @param address 账户地址
     * @return
     */
    public Alias getAliasByAddress(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
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
    @Transactional
    @Override
    public int save(Alias alias) {
//        if(null == alias){
//            return 2;
//        }
//        if(StringUtils.validAddress(alias.getAddress())){
//            return 3;
//        }
//        if(alias.getBlockHeight() < 0){
//            return 4;
//        }
//        if(StringUtils.isBlank(alias.getAlias())){
//            return 5;
//        }
//        if(getAliasByAddress(alias.getAddress())!=null){
//            return 6;
//        }
//        if(getByKey(alias.getAlias())!=null){
//            return 7;
//        }

        int result = aliasMapper.insert(alias);
        if(result == 1){
            //保存成功后，同步别名到静态缓存中
            AliasContext.put(alias);
        }
        return result;
    }

    @Transactional
    @Override
    public int update(Alias alias) {
        return aliasMapper.updateByPrimaryKey(alias);
    }

    /**
     * 删除别名，根据账户地址删除
     *
     * @param id 账户地址
     * @return
     */
    @Transactional
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
        return aliasMapper.selectByPrimaryKey(id);
    }


    /**
     * 删除别名，根据高度删除
     *
     * @param height
     * @return
     */
    @Transactional
    public void deleteByHeight(long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        //删除缓存中的别名
        AliasContext.removeByHeight(height);
        aliasMapper.deleteBySearchable(searchable);
    }
}
