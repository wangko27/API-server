package io.nuls.api.server.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ApplicationContext.xml")
@Transactional
public class ReportDAOTest {

    @Autowired
    private ReportDAO reportDAO;

    @Test
    public void testTaskDB() {
        try {
            reportDAO.txHistory();
            reportDAO.mined();
            reportDAO.balance();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
