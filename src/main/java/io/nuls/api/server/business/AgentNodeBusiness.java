package io.nuls.api.server.business;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.entity.AgentNode;
import io.nuls.api.server.dao.mapper.AgentNodeMapper;
import io.nuls.api.server.dao.util.SearchOperator;
import io.nuls.api.server.dao.util.Searchable;
import io.nuls.api.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 节点
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Service
public class AgentNodeBusiness{

    @Autowired
    private AgentNodeMapper agentNodeMapper;

    /**
     * 获取列表
     * @param agentName 节点名称 (搜索用)
     * @return
     */
    public PageInfo<AgentNode> getList(String agentName, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Searchable searchable = new Searchable();
        if(StringUtils.isNotBlank(agentName)){
            searchable.addCondition("agent_name", SearchOperator.like, agentName);
        }
        PageInfo<AgentNode> page = new PageInfo<>(agentNodeMapper.selectList(searchable));
        return page;
    }

    /**
     * 获取节点详情
     * @param agentAddress 创建地址
     * @return
     */
    public AgentNode getAgentDetail(String agentAddress) {
        Searchable searchable = new Searchable();
        searchable.addCondition("agent_address", SearchOperator.eq, agentAddress);
        return agentNodeMapper.selectBySearchable(searchable);
    }

    /**
     * 根据id查询
     * @param txHash 创建地址
     * @return
     */
    public AgentNode getDetail(String txHash) {
        return agentNodeMapper.selectByPrimaryKey(txHash);
    }

    /**
     * 保存
     * @param entity 实体
     * @return 1成功，其他失败
     */
    @Transactional
    public int insert(AgentNode entity) {
        return agentNodeMapper.insert(entity);
    }

    /**
     * 根据主键删除节点
     * @param txHash  主键 txhash
     * @return
     */
    @Transactional
    public int deleteById(String txHash){
        return agentNodeMapper.deleteByPrimaryKey(txHash);
    }

    /**
     * 根据高度删除某高度所有节点
     * @param height  高度
     * @return
     */
    @Transactional
    public int deleteByHeight(Long height){
        Searchable searchable = new Searchable();
        searchable.addCondition("block_height", SearchOperator.eq, height);
        return agentNodeMapper.deleteBySearchable(searchable);
    }




}
