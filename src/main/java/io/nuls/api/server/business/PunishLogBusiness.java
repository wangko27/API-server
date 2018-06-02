package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.PunishLog;
import io.nuls.api.server.dao.mapper.PunishLogMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 处罚日志
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class PunishLogBusiness {

    @Autowired
    private PunishLogMapper punishLogMapper;

    /**
     * 处罚列表
     * @param address 账户地址
     * @return
     */
    public PageInfo<PunishLog> getList(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        PageInfo<PunishLog> page = new PageInfo<>(punishLogMapper.selectList(searchable));
        return page;
    }

    /**
     * 保存
     * @param entity 实体
     * @return 1成功，其他失败
     */
    @Transactional
    public int insert(PunishLog entity){
        return punishLogMapper.insert(entity);
    }

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    public PunishLog getDetail(String id){
        return punishLogMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据主键删除
     * @param id
     * @return 1成功，其他失败
     */
    @Transactional
    public int deleteById(String id){
        return punishLogMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据高度删除
     * @param height 高度
     * @return 1成功，其他失败
     */
    @Transactional
    public int deleteByHeight(Long height){
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return punishLogMapper.deleteBySearchable(searchable);
    }

}
