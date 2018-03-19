package io.nuls.api.server.task;

import io.nuls.api.counter.QueryCounter;
import io.nuls.api.server.dao.ReportDao;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MinedTask {

    @Autowired
    private ReportDao reportDao;

    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void execute() {
        Log.debug("start to execute mined report");
        reportDao.mined();
        QueryCounter.setMinedModify(1);
        Log.debug("the mined report completed");
    }
}


