package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.Constant;
import io.nuls.api.entity.AddressRewardDetail;
import io.nuls.api.entity.Transaction;
import io.nuls.api.entity.Utxo;
import io.nuls.api.server.dao.mapper.AddressRewardDetailMapper;
import io.nuls.api.server.dao.util.EhcacheUtil;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 账户奖励
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Service
public class AddressRewardDetailBusiness implements BaseService<AddressRewardDetail, Long> {

    @Autowired
    private AddressRewardDetailMapper addressRewardDetailMapper;

    /**
     * 获取列表
     *
     * @param address 奖励的账户地址
     * @return
     */
    public PageInfo<AddressRewardDetail> getList(String address, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        PageInfo<AddressRewardDetail> page = new PageInfo<>(addressRewardDetailMapper.selectList(searchable));
        return page;
    }

    /**
     * 根据高度删除某高度所有数据
     * @param height
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHeight(Long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return addressRewardDetailMapper.deleteBySearchable(searchable);
    }

    /**
     * 新增奖励
     *
     * @param addressRewardDetail 需要新增的实体
     * @return 新增结果 1成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(AddressRewardDetail addressRewardDetail) {
        return addressRewardDetailMapper.insert(addressRewardDetail);
    }

    /**
     * 根据交易，获取该交易中，所有奖励的list
     * @param tx
     * @return
     */
    public List<AddressRewardDetail> getRewardList(Transaction tx) {
        List<AddressRewardDetail> utxoList = new ArrayList<>();
        if (tx.getOutputs() != null && !tx.getOutputs().isEmpty()) {
            for (Utxo utxo : tx.getOutputs()) {
                AddressRewardDetail detail = new AddressRewardDetail();
                detail.setAddress(utxo.getAddress());
                detail.setCreateTime(tx.getCreateTime());
                detail.setAmount(utxo.getAmount());
                detail.setBlockHeight(tx.getBlockHeight());
                detail.setTxHash(tx.getHash());
                utxoList.add(detail);
            }
        }
        return utxoList;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void saveAll(List<AddressRewardDetail> list) {
        if (list.size() > 0) {
            addressRewardDetailMapper.insertByBatch(list);
        }
    }

    /**
     * 根据主键修改
     *
     * @param addressRewardDetail
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(AddressRewardDetail addressRewardDetail) {
        return addressRewardDetailMapper.updateByPrimaryKey(addressRewardDetail);
    }

    /**
     * 根据主键删除数据
     *
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(Long id) {
        return addressRewardDetailMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据主键查询数据
     *
     * @param id
     * @return
     */
    @Override
    public AddressRewardDetail getByKey(Long id) {
        return addressRewardDetailMapper.selectByPrimaryKey(id);
    }

    /**
     * 统计某地址总的奖励
     *
     * @return
     */
    public Long selectSumReward(String address) {
        if (StringUtils.validAddress(address)) {
            Searchable searchable = new Searchable();
            searchable.addCondition("address", SearchOperator.eq, address);
            return addressRewardDetailMapper.selectSumReward(searchable);
        } else {
            return 0L;
        }
    }

    /**
     * 统计某地址总的奖励 （根据时间统计）
     *
     * @return
     */
    public Long selectDayofReward(Long time) {
        Searchable searchable = new Searchable();
        searchable.addCondition("create_time", SearchOperator.gte, time - Constant.MILLISECONDS_TIME_DAY);
        searchable.addCondition("create_time", SearchOperator.lt, time);
        return addressRewardDetailMapper.selectSumReward(searchable);
    }

    /**
     * 获取某地址最后奖励的高度
     * @param address
     * @return
     */
    public Long getLastRewardHeight(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        return addressRewardDetailMapper.getLastRewardHeight(searchable);
    }
}
