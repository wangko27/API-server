package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.PunishLog;
import io.nuls.api.entity.TxData;
import io.nuls.api.server.dao.mapper.PunishLogMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.glassfish.grizzly.compression.lzma.impl.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 处罚日志
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Service
public class PunishLogBusiness implements BaseService<PunishLog, Long> {

    @Autowired
    private PunishLogMapper punishLogMapper;

    /**
     * 处罚列表
     *
     * @param address 账户地址
     * @return
     */
    public PageInfo<PunishLog> getList(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if(!StringUtils.validAddress(address)){
            return null;
        }
        searchable.addCondition("address", SearchOperator.eq, address);
        PageInfo<PunishLog> page = new PageInfo<>(punishLogMapper.selectList(searchable));
        return page;
    }

    /**
     * 根据状态，统计数量
     * @param type
     * @return
     */
    public int getCountByStatus(Integer type,String address){
        Searchable searchable = new Searchable();
        if(StringUtils.validAddress(address)){
            searchable.addCondition("address", SearchOperator.eq, address);
        }
        if(null != type){
            searchable.addCondition("type", SearchOperator.eq, type);
        }
        return punishLogMapper.selectCountSearchable(searchable);
    }

    /**
     * 根据高度删除
     *
     * @param height 高度
     * @return 1成功，其他失败
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHeight(Long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return punishLogMapper.deleteBySearchable(searchable);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(PunishLog punishLog) {
        return punishLogMapper.insert(punishLog);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveList(List<TxData> list) {
        for (TxData data : list) {
            PunishLog log = (PunishLog) data;
            punishLogMapper.insert(log);
        }
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<PunishLog> list) {
        if(list.size() > 0){
            punishLogMapper.insertByBatch(list);
        }
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(PunishLog punishLog) {
        return punishLogMapper.updateByPrimaryKey(punishLog);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(Long id) {
        return punishLogMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PunishLog getByKey(Long id) {
        return punishLogMapper.selectByPrimaryKey(id);
    }
}
