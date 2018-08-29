package io.nuls.api.server.resources;

import io.nuls.api.utils.RestFulUtils;
import org.springframework.stereotype.Component;

@Component
public class GetBlock {

    public void getBlock(long height) {
        RestFulUtils.getInstance().get("/header/height/" + height, null);
    }
}
