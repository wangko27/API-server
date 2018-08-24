package io.nuls.api.server.business;

import io.nuls.api.context.NulsContext;
import io.nuls.api.entity.NulsStatistics;
import org.springframework.stereotype.Service;

/**
 * Created by inchain on 2018/8/23.
 */
@Service
public class AssetsBusiness {
    public NulsStatistics get(String key) {
        return NulsContext.getNulsStatistics(key);
    }
}