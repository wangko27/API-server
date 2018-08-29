package io.nuls.api.server.business;

import java.io.Serializable;

public interface BaseService<M, ID extends Serializable> {

    int save(M m);

    int update(M m);

    int deleteByKey(ID id);

    M getByKey(ID id);
}
