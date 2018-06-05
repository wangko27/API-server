package io.nuls.api.server.task;

import io.nuls.api.context.HistoryContext;
import io.nuls.api.server.business.BlockBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Description:
 * Author: zsj
 * Date:  2018/6/5 0005
 */
@Component
public class TransactionHistoryTask {

    @Autowired
    private BlockBusiness blockBusiness;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void execute(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long time = cal.getTime().getTime();
        for(int i = 1; i <= 14; i++){
            Integer count = blockBusiness.getTxcountByTime(time-86400000,time);
            time = time - 86400000;
            if(null == count){
                continue;
            }
            HashMap<String,String> arr = new HashMap<>();
            arr.put("value",count+"");
            arr.put("date",time+"");
            HistoryContext.add(arr);
        }
    }
}
