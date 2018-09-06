package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.entity.Deposit;
import io.nuls.api.entity.RpcClientResult;
import io.nuls.api.server.dao.mapper.AgentNodeMapper;
import io.nuls.api.server.dao.mapper.DepositMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.server.dto.AgentDto;
import io.nuls.api.server.dto.AgentNodeDto;
import io.nuls.api.server.dto.UtxoDto;
import io.nuls.api.utils.RestFulUtils;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 节点
 * Author: moon
 * Date:  2018/5/29 0029
 */
@Service
public class AgentNodeBusiness implements BaseService<AgentNode, String> {

    @Autowired
    private AgentNodeMapper agentNodeMapper;
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private DepositBusiness depositBusiness;


    /**
     * 根据页数查询节点列表，由于信用值没办法及时获取，只能每次调用都去链上查询
     *
     * @param agentName 节点名称 (搜索用)
     * @return
     */
    public RpcClientResult getList(String agentName, int pageNumber, int pageSize) {
        Map<String, String> param = new HashMap<>(3);
        if (StringUtils.isNotBlank(agentName)) {
            param.put("keyword", agentName);
        }
        param.put("pageNumber", pageNumber + "");
        param.put("pageSize", pageSize + "");
        return RestFulUtils.getInstance().get("/consensus/agent/list", param);
    }

    /**
     * 获取我委托了的节点的列表
     * @param address
     * @param keyword
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public PageInfo<AgentDto> getMy(String address, String keyword, int pageNumber, int pageSize){
        List<String> depositedHashList = depositBusiness.getDepositedAgentByAddress(address);
        List<AgentDto> agentDtoList = new ArrayList<>();
        if(null != depositedHashList){
            for(String hash:depositedHashList){
                AgentDto map = IndexContext.getNodeByAgentHash(hash);
                if(null != map){
                    agentDtoList.add(map);
                }
            }
        }
        int start = (pageNumber-1)*pageSize;
        int end = start+pageSize;
        List<AgentDto> list = new ArrayList<>();
        if(StringUtils.isNotBlank(keyword)){
            for(AgentDto agentDto:agentDtoList){
                //搜索
                keyword = keyword.toUpperCase();
                boolean b = agentDto.getAgentId().indexOf(keyword) >= 0;
                b = b || agentDto.getAgentAddress().toUpperCase().equals(keyword) || agentDto.getPackingAddress().toUpperCase().equals(keyword);
                if (StringUtils.isNotBlank(agentDto.getAgentName())) {
                    b = b || agentDto.getAgentName().toUpperCase().indexOf(keyword) >= 0;
                }
                if (b) {
                    list.add(agentDto);
                }
                continue;
            }
        }else{
            list = agentDtoList;
        }
        if(list.size()<end){
            end = list.size();
        }
        List<AgentDto> returnList = new ArrayList<>();
        for(int i =start;i<end;i++){
            returnList.add(list.get(i));
        }
        PageInfo<AgentDto> pageAgent = new PageInfo<>(returnList);
        pageAgent.setPageSize(pageSize);
        pageAgent.setPageNum(pageNumber);
        pageAgent.setTotal(list.size());
        return pageAgent;
    }


    /**
     * 查询全网共识信息
     *
     * @return
     */
    public RpcClientResult getConsensus() {
        return RestFulUtils.getInstance().get("/consensus", null);
    }


    /**
     * 根据agentHash查询某个节点的详细信息（加载某信息的信用值、是否正在共识，这些信息只能去链上查询）
     *
     * @param agentHash
     * @return
     */
    public RpcClientResult getAgentByAddressWithRpc(String agentHash) {
        return RestFulUtils.getInstance().get("/consensus/agent/" + agentHash, null);
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
        searchable.addCondition("delete_hash", SearchOperator.isNull, null);
        return agentNodeMapper.selectBySearchable(searchable);
    }

    /**
     * 根据高度删除某高度所有节点
     *
     * @param height 高度
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(AgentNode agentNode) {
        return agentNodeMapper.insert(agentNode);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAll(List<AgentNode> list) {
        if (list.size() > 0) {
            return agentNodeMapper.insertByBatch(list);
        }
        return 0;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteByKey(String id) {
        return agentNodeMapper.deleteByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void stopAgent(AgentNode agentNode, String txHash) {
        agentNode = agentNodeMapper.selectByPrimaryKey(agentNode.getTxHash());
        agentNode.setDeleteHash(txHash);
        agentNodeMapper.updateByPrimaryKey(agentNode);
        depositMapper.deleteByAgentHash(agentNode.getTxHash(), txHash);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void stopAgentByRedPublish(String agentAddress, String txHash) {
        Searchable searchable = new Searchable();
        searchable.addCondition("agent_address", SearchOperator.eq, agentAddress);
        searchable.addCondition("delete_hash", SearchOperator.isNull, null);
        AgentNode agentNode = agentNodeMapper.selectBySearchable(searchable);
        agentNode.setDeleteHash(txHash);
        agentNodeMapper.updateByPrimaryKey(agentNode);
        depositMapper.deleteByAgentHash(agentNode.getTxHash(), txHash);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
    public List<AgentNodeDto> selectTotalpackingCount() {
        Map<String,AgentNodeDto> agentNodeDtoMap = new HashMap<>();
        Searchable searchable = new Searchable();
        /**
         * 出块数量大于零的 没有删除的节点
         */
        List<AgentNodeDto>  agentNodeDtoList = new ArrayList<>();
        searchable.addCondition("total_packing_count", SearchOperator.gt, 0);
        searchable.addCondition("delete_hash", SearchOperator.isNull, null);
        List<AgentNode> agentNodeList = agentNodeMapper.selectList(searchable);
        long packIngAccount = 0;
        long totalReward = 0;
        long lastRewardHeight = 0;
        AgentNodeDto agentNodeDto = null;
        AgentNodeDto mapAgentNodeDto = null;
        /*统计出块数量和总收益*/
        for(AgentNode agentNode:agentNodeList){
            /*已经存在，统计出块数量，总收益，重置最后收益区块*/
            if(agentNodeDtoMap.containsKey(agentNode.getAgentAddress())){
                packIngAccount = agentNode.getTotalPackingCount()==null?0:agentNode.getTotalPackingCount();
                totalReward = agentNode.getTotalReward()==null?0:agentNode.getTotalReward();
                lastRewardHeight = agentNode.getLastRewardHeight()==null?0:agentNode.getLastRewardHeight();
                mapAgentNodeDto = agentNodeDtoMap.get(agentNode.getAgentAddress());
                mapAgentNodeDto.setTotalPackingCount(agentNodeDto.getTotalPackingCount()+packIngAccount);
                mapAgentNodeDto.setTotalReward(agentNodeDto.getTotalReward()+totalReward);
                if(mapAgentNodeDto.getLastRewardHeight() < lastRewardHeight){
                    mapAgentNodeDto.setLastRewardHeight(lastRewardHeight);
                }
                agentNodeDtoMap.put(agentNode.getAgentAddress(),mapAgentNodeDto);
            }else{
                /*不存在，直接新增*/
                agentNodeDto = new AgentNodeDto(agentNode,0,"");
                agentNodeDtoMap.put(agentNode.getAgentAddress(),agentNodeDto);
            }
        }
        agentNodeDtoList =new ArrayList<>(agentNodeDtoMap.values());
        /*根据出块数量从高到底排序*/
        Collections.sort(agentNodeDtoList, new Comparator<AgentNodeDto>() {
            @Override
            public int compare(AgentNodeDto o1, AgentNodeDto o2) {
                return o2.getTotalPackingCount().compareTo(o1.getTotalPackingCount());
            }
        });
        return agentNodeDtoList;
    }
}
