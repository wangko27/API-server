package io.nuls.api.server.task;

import io.nuls.api.utils.log.Log;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Flyglede on 2018/8/23.
 */
@Component
public class AssetsBrowseTask {
    /**
     * 定时从数据库中将统计数据写入Ehcache缓存
     */
    public void execute() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Count Data Write Time>>>>>>>>>>>>>>>>"+(sdf.format(new Date())));
    }
}