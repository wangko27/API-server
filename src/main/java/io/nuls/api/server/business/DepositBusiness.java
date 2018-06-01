package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.Deposit;
import io.nuls.api.server.dao.mapper.DepositMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 委托
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class DepositBusiness {

    @Autowired
    private DepositMapper depositMapper;

    /**
     * 委托列表
     * @param address 账户地址
     * @return
     */
    public PageInfo<Deposit> getList(String address,int pageNumber,int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        PageInfo<Deposit> page = new PageInfo<>(depositMapper.selectList(searchable));
        return page;
    }

    /**
     * 新增委托 检查被委托的节点是否已被委托满
     * @param entity
     * @return
     */
    @Transactional
    public int insert(Deposit entity){
        return depositMapper.insert(entity);
    }

    /**
     * 根据主键查询详情
     * @param txHash 主键
     * @return
     */
    public Deposit getDetail(String txHash){
        return depositMapper.selectByPrimaryKey(txHash);
    }

    /**
     * 根据主键删除
     * @param txHash 主键
     * @return
     */
    @Transactional
    public int deleteById(String txHash){
        return depositMapper.deleteByPrimaryKey(txHash);
    }

    /**
     * 根据高度删除
     * @param height 高度
     * @return
     */
    @Transactional
    public int deleteByHeight(Long height){
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return depositMapper.deleteBySearchable(searchable);
    }

}
