package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.Deposit;
import io.nuls.api.server.dao.mapper.AgentNodeMapper;
import io.nuls.api.server.dao.mapper.DepositMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 委托
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class DepositBusiness implements BaseService<Deposit, String> {

    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private AgentNodeMapper agentNodeMapper;

    /**
     * 根据地址，获取某地址已委托的列表
     * @param address
     * @return
     */
    public List<String> getDepositedAgentByAddress(String address){
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        searchable.addCondition("a2.delete_hash", SearchOperator.isNull, null);
        return depositMapper.selectDepositedAgentByAddress(searchable);
    }

    /**
     * 委托列表
     * @param address 账户地址
     * @return
     */
    public PageInfo<Deposit> getList(String address, String agentHash, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        if (StringUtils.validHash(agentHash)) {
            searchable.addCondition("agent_hash", SearchOperator.eq, agentHash);
        }
        searchable.addCondition("delete_hash", SearchOperator.isNull, null);
        PageInfo<Deposit> page = new PageInfo<>(depositMapper.selectList(searchable));
        return page;
    }

    /**
     * 获取列表，agentHash模糊匹配
     * @param address
     * @param agentHash
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<Deposit> getListWithSearch(String address, String agentHash, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        if (StringUtils.validHash(agentHash)) {
            searchable.addCondition("agent_hash", SearchOperator.suffixLike, agentHash);
        }
        searchable.addCondition("delete_hash", SearchOperator.isNull, null);
        PageInfo<Deposit> page = new PageInfo<>(depositMapper.selectList(searchable));
        return page;
    }

    /**
     * 新增委托 检查被委托的节点是否已被委托满
     *
     * @param entity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insert(Deposit entity) {
        return depositMapper.insert(entity);
    }

    /**
     * 根据主键查询详情
     *
     * @param txHash 主键
     * @return
     */
    public Deposit getDetail(String txHash) {
        return depositMapper.selectByPrimaryKey(txHash);
    }

    /**
     * 根据高度删除
     *
     * @param height 高度
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHeight(Long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return depositMapper.deleteBySearchable(searchable);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(Deposit deposit) {
        AgentNode agentNode = agentNodeMapper.selectByPrimaryKey(deposit.getAgentHash());
        agentNode.setTotalDeposit(agentNode.getTotalDeposit() + deposit.getAmount());
        agentNode.setDepositCount(agentNode.getDepositCount() + 1);
        agentNodeMapper.updateByPrimaryKey(agentNode);
        return depositMapper.insert(deposit);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateAgentNodeByDeposit(Deposit deposit) {
        AgentNode agentNode = agentNodeMapper.selectByPrimaryKey(deposit.getAgentHash());
        agentNode.setTotalDeposit(agentNode.getTotalDeposit() + deposit.getAmount());
        agentNode.setDepositCount(agentNode.getDepositCount() + 1);
        return agentNodeMapper.updateByPrimaryKey(agentNode);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAll(List<Deposit> list){
        if(list.size()>0){
            return depositMapper.insertByBatch(list);
        }
        return 0;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(Deposit deposit) {
        return depositMapper.updateByPrimaryKey(deposit);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(String s) {
        Deposit deposit = depositMapper.selectByPrimaryKey(s);
        AgentNode agentNode = agentNodeMapper.selectByPrimaryKey(deposit.getAgentHash());
        agentNode.setTotalDeposit(agentNode.getTotalDeposit() - deposit.getAmount());
        agentNode.setDepositCount(agentNode.getDepositCount() - 1);
        agentNodeMapper.updateByPrimaryKey(agentNode);
        return depositMapper.deleteByPrimaryKey(s);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int cancelDeposit(Deposit deposit, String deleteHash) {
        deposit = depositMapper.selectByPrimaryKey(deposit.getTxHash());
        AgentNode agentNode = agentNodeMapper.selectByPrimaryKey(deposit.getAgentHash());
        agentNode.setTotalDeposit(agentNode.getTotalDeposit() - deposit.getAmount());
        agentNode.setDepositCount(agentNode.getDepositCount() - 1);
        agentNodeMapper.updateByPrimaryKey(agentNode);
        deposit.setDeleteHash(deleteHash);
        return depositMapper.updateByPrimaryKey(deposit);
    }

    @Override
    public Deposit getByKey(String s) {
        return depositMapper.selectByPrimaryKey(s);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollbackCancelDeposit(String deleteHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("delete_hash", SearchOperator.eq, deleteHash);
        Deposit deposit = depositMapper.selectBySearchable(searchable);
        deposit.setDeleteHash(null);
        depositMapper.updateByPrimaryKey(deposit);

        AgentNode agentNode = agentNodeMapper.selectByPrimaryKey(deposit.getAgentHash());
        agentNode.setTotalDeposit(agentNode.getTotalDeposit() + deposit.getAmount());
        agentNode.setDepositCount(agentNode.getDepositCount() + 1);
        agentNodeMapper.updateByPrimaryKey(agentNode);
    }

    /**
     * 查询总委托金额 根据地址，查询某人的委托总额
     * @return
     */
    public Long selectTotalAmount(String address){
        Searchable searchable = new Searchable();
        searchable.addCondition("address", SearchOperator.eq, address);
        searchable.addCondition("delete_hash", SearchOperator.isNull, null);
        return depositMapper.selectTotalAmount(searchable);
    }
}
