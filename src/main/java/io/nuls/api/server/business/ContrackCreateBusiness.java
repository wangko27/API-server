package io.nuls.api.server.business;

import io.nuls.api.entity.ContrackCreateInfo;
import io.nuls.api.server.dao.mapper.ContrackCreateInfoMapper;
import io.nuls.api.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @desription: 智能合约创建数据处理器，包括创建数据的回滚、查询与存储等
 * @author: qinyf
 * @date: 2018/9/11
 */
@Service
public class ContrackCreateBusiness implements BaseService<ContrackCreateInfo, String> {

    @Autowired
    private ContrackCreateInfoMapper contrackCreateInfoMapper;

    /**
     * 保存智能合约地址
     *
     * @param contrackCreateInfo 实体
     * @return 1成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save(ContrackCreateInfo contrackCreateInfo) {
        return contrackCreateInfoMapper.insert(contrackCreateInfo);
        
    }

    /**
     * 批量保存
     * @param list
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int saveAll(List<ContrackCreateInfo> list) {
        if (list.size() > 0) {
            return contrackCreateInfoMapper.insertByBatch(list);
        }
        return 0;
    }

    /**
     * 更新智能合约创建过程数据
     *
     * @param contrackCreateInfo 实体
     * @return 1成功，其他失败
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(ContrackCreateInfo contrackCreateInfo) {
        return contrackCreateInfoMapper.updateByPrimaryKey(contrackCreateInfo);
    }

    /**
     * 根据主键-创建交易哈希删除节点
     *
     * @param createTxHash 主键
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int deleteByKey(String createTxHash) {
        return contrackCreateInfoMapper.deleteByPrimaryKey(createTxHash);
    }

    /**
     * 根据创建交易哈希查询
     *
     * @param createTxHash 创建交易哈希
     * @return
     */
    @Override
    public ContrackCreateInfo getByKey(String createTxHash) {
        return contrackCreateInfoMapper.selectByPrimaryKey(createTxHash);
    }

    /**
     * 删除智能合约创建过程数据，根据交易哈希批量删除，只有回滚的时候才会调用
     *
     * @param txHashList
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteList(List<String> txHashList) {
        try {
            System.out.println("createdata deleteList====="+ JSONUtils.obj2json(txHashList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(null != txHashList){
            contrackCreateInfoMapper.deleteList(txHashList);
        }
    }
}
