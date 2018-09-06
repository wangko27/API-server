package io.nuls.api.server.task;

import io.nuls.api.server.business.BlockBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 统计交易历史，每天凌晨12点统计一次
 * Author: moon
 * Date:  2018/6/5 0005
 */
@Component
public class TransactionHistoryTask {

    @Autowired
    private BlockBusiness blockBusiness;

    public void execute(){
        /*14天交易历史*/
        blockBusiness.initHistory(2);
    }
}
