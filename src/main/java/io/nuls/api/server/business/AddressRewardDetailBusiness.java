package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.AddressRewardDetail;
import io.nuls.api.server.dao.mapper.AddressRewardDetailMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 账户奖励
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AddressRewardDetailBusiness {

    @Autowired
    private AddressRewardDetailMapper addressRewardDetailMapper;

    /**
     * 获取列表
     * @param address 奖励的账户地址
     * @return
     */
    public PageInfo<AddressRewardDetail> getList(String address,int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        PageInfo<AddressRewardDetail> page = new PageInfo<>(addressRewardDetailMapper.selectList(searchable));
        return page;
    }

    /**
     * 新增奖励
     * @param entity 需要新增的实体
     * @return 新增结果 1成功，其他失败
     */
    public int insert(AddressRewardDetail entity){
        return addressRewardDetailMapper.insert(entity);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public AddressRewardDetail getDetail(Long id){
        return addressRewardDetailMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据主键删除数据
     * @param id
     * @return
     */
    public int deleteById(Long id){
        return addressRewardDetailMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据高度删除某高度所有数据
     * @param height
     * @return
     */
    public int deleteByHeight(Long height){
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return addressRewardDetailMapper.deleteBySearchable(searchable);
    }

}
