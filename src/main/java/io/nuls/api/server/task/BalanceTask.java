package io.nuls.api.server.task;

import io.nuls.api.server.dao.ReportDao;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class BalanceTask {

    @Autowired
    private ReportDao reportDao;

    @Transactional(propagation= Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void execute() {
        Log.debug("start to execute balance report");
        reportDao.balance();
        Log.debug("the balance report completed");
    }
}
