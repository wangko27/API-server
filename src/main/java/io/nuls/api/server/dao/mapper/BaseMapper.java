package io.nuls.api.server.dao.mapper;

import io.nuls.api.server.dao.util.Searchable;

import java.io.Serializable;
import java.util.List;

/**
 * mybatis基础查询类，封装了对象的基本查询方法,
 * 以下方法sql实现方式，都需自行写在mapper.xml里
 * @author zhouwei
 *
 */
@MyBatisMapper
public interface BaseMapper<M, ID extends Serializable> {

	int insert(M m);

    int insertSelective(M m);
    
    int updateByPrimaryKey(M m);
    
    int updateByPrimaryKeySelective(M m);
    
    int deleteByPrimaryKey(ID id);

    int deleteBySearchable(Searchable searchable);
    
    M selectByPrimaryKey(ID id);
    
    long selectCount(Searchable searchable);
    
    List<M> selectList(Searchable searchable);
    
    M selectBySearchable(Searchable searchable);
    
    long exists(ID id);

    long exists(Searchable searchable);

    List<M> findAll();

}
