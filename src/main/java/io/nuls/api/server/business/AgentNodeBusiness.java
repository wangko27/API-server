package io.nuls.api.server.business;

import io.nuls.api.constant.ErrorCode;
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
        /*PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if (StringUtils.isNotBlank(agentName)) {
            searchable.addCondition("agent_name", SearchOperator.like, agentName);
        }
        PageInfo<AgentNode> page = new PageInfo<>(agentNodeMapper.selectList(searchable));
        return page;*/
        ///consensus/agent/list
        Map<String,String> param = new HashMap<>();
        if (StringUtils.isNotBlank(agentName)) {
            param.put("keyword", agentName);
        }
        param.put("pageNumber", pageNumber+"");
        param.put("pageSize", pageSize+"");
        return RestFulUtils.getInstance().get("/consensus/agent/list", param);
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
    @Transactional
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
    @Transactional
    @Override
    public int save(AgentNode agentNode) {
        return agentNodeMapper.insert(agentNode);
    }

    @Transactional
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
    @Transactional
    public int deleteByKey(String id) {

        return agentNodeMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void stopAgent(AgentNode agentNode, String txHash) {
        agentNode = agentNodeMapper.selectByPrimaryKey(agentNode.getTxHash());
        agentNode.setDeleteHash(txHash);
        agentNodeMapper.updateByPrimaryKey(agentNode);
        depositMapper.deleteByAgentHash(agentNode.getTxHash(), txHash);
    }

    @Transactional
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
     * 统计出块排名
     * @return
     */
    public List<AgentNodeDto> selectTotalpackingCount(){
        //getDetailByAgentAddress
        List<AgentNodeDto> agentNodeDtoList = agentNodeMapper.selectTotalpackingCount(new Searchable());
        /*for(AgentNode agentNode:agentNodeDtoList){
            RpcClientResult result = getDetailByAgentAddress(agentNode.getAgentAddress());
            if(result.isSuccess()){
                HashMap<String,Object> data = (HashMap)result.getData();
                Integer.parseInt(data.get("status")+"");
            }
        }*/
        //todo 如何统计当前节点状态等信息
        return agentNodeDtoList;
    }

    /**
     * 根据Searchable 查询数据条数
     * @return
     */
    public Integer selectTotalCount(){
        return agentNodeMapper.selectTotalCount(new Searchable());
    }
}
