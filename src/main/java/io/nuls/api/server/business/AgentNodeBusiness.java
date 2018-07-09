package io.nuls.api.server.business;

import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.dao.mapper.AgentNodeMapper;
import io.nuls.api.server.dao.mapper.DepositMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 节点
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AgentNodeBusiness implements BaseService<AgentNode, String> {

    @Autowired
    private AgentNodeMapper agentNodeMapper;
    @Autowired
    private DepositMapper depositMapper;

    /**
     * 根据页数查询节点列表，由于信用值没办法及时获取，只能每次调用都去链上查询
     * @param agentName 节点名称 (搜索用)
     * @return
     */
    public RpcClientResult getList(String agentName, int pageNumber, int pageSize) {
        Map<String,String> param = new HashMap<>();
        if (StringUtils.isNotBlank(agentName)) {
            param.put("keyword", agentName);
        }
        param.put("pageNumber", pageNumber+"");
        param.put("pageSize", pageSize+"");
        return RestFulUtils.getInstance().get("/consensus/agent/list", param);
    }

    /**
     * 查询全网共识信息
     * @return
     */
    public RpcClientResult getConsensus() {
        return RestFulUtils.getInstance().get("/consensus", null);
    }


    /**
     * 根据agentHash查询某个节点的详细信息（加载某信息的信用值、是否正在共识，这些信息只能去链上查询）
     * @param agentHash
     * @return
     */
    public RpcClientResult getAgentByAddressWithRpc(String agentHash) {
        return RestFulUtils.getInstance().get("/consensus/agent/"+agentHash,null);
    }

    /**
     * 获取节点详情
     *
     * @param address
     * @return
     */
    public AgentNode getAgentByAddress(String address) {
        Searchable searchable = new Searchable();
        searchable.addCondition("packing_address", SearchOperator.eq, address);
        searchable.addCondition("delete_hash", SearchOperator.isNull,null);
        return agentNodeMapper.selectBySearchable(searchable);
    }

    /**
     * 根据高度删除某高度所有节点
     *
     * @param height 高度
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByHeight(Long height) {
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return agentNodeMapper.deleteBySearchable(searchable);
    }

    /**
     * 保存
     *
     * @param agentNode 实体
     * @return 1成功，其他失败
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(AgentNode agentNode) {
        if(null == getAgentByAddress(agentNode.getPackingAddress())){
            return agentNodeMapper.insert(agentNode);
        }
        return 0;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAll(List<AgentNode> list){
        if(list.size() > 0){
            return agentNodeMapper.insertByBatch(list);
        }
        return 0;
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(AgentNode agentNode) {
        return agentNodeMapper.updateByPrimaryKey(agentNode);
    }

    /**
     * 根据主键删除节点
     *
     * @param id 主键 txhash
     * @return
     */
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(String id) {
        return agentNodeMapper.deleteByPrimaryKey(id);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void stopAgent(AgentNode agentNode, String txHash) {
        agentNode = agentNodeMapper.selectByPrimaryKey(agentNode.getTxHash());
        agentNode.setDeleteHash(txHash);
        agentNodeMapper.updateByPrimaryKey(agentNode);
        depositMapper.deleteByAgentHash(agentNode.getTxHash(), txHash);
    }

    @Transactional(propagation= Propagation.REQUIRED, rollbackFor = Exception.class)
    public void rollbackStopAgent(String deleteHash) {
        agentNodeMapper.rollbackStopAgent(deleteHash);
        depositMapper.rollbackStopAgent(deleteHash);
    }

    /**
     * 根据id查询
     *
     * @param id 创建地址
     * @return
     */
    @Override
    public AgentNode getByKey(String id) {
        return agentNodeMapper.selectByPrimaryKey(id);
    }

    /**
     * 统计出块排名 没出块的就不排名，只统计已经在出块的
     * @return
     */
    public List<AgentNodeDto> selectTotalpackingCount(){
        Searchable searchable = new Searchable();
        /**
         * 出块数量大于零的 没有删除的节点
         */
        searchable.addCondition("total_packing_count", SearchOperator.gt, 0);
        searchable.addCondition("delete_hash", SearchOperator.isNull,null);
        List<AgentNodeDto> agentNodeDtoList = agentNodeMapper.selectTotalpackingCount(searchable);
        return agentNodeDtoList;
    }

    /**
     * 根据Searchable 查询数据条数 不传就查询全部全部共识信息数据
     * @return
     */
    public Integer selectTotalCount(){
        return agentNodeMapper.selectTotalCount(new Searchable());
    }
}
