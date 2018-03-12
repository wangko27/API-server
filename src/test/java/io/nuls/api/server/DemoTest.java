package io.nuls.api.server;

import io.nuls.api.server.business.DemoBusiness;
import io.nuls.api.server.entity.Demo;
import io.nuls.api.server.entity.DemoParam;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ApplicationContext.xml")
@SqlGroup({
        @Sql("classpath:createDemo.sql"),
        @Sql(scripts = "classpath:dropDemo.sql", executionPhase = AFTER_TEST_METHOD)
})
public class DemoTest {

    @Autowired
    protected DemoBusiness demoBusiness = null;



    @Test
    public void test() {
        testInsert();
        testSelect();
        testUpdate();
    }


    public void testInsert() {
        Demo demo = new Demo();
        demo.setBytecol((byte) 1);
        demo.setShortcol((short) 2);
        demo.setIntcol(3);
        demo.setFloatcol(4.0f);
        demo.setDoublecol(5.0);
        demo.setStringcol1("A");
        demo.setStringcol2("String column2");
        demo.setDatecol(new Date());
        demo.setBlobcol("BLOB COLUMN".getBytes());

        try {
            int count = this.demoBusiness.insertSelective(demo);
            if (count == 1 && demo.getId() > 0) {
                System.out.println("insert success！");
            } else {
                Assert.fail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void testSelect() {
        DemoParam param = new DemoParam();
        param.createCriteria().andBytecolEqualTo((byte) 1);
        try {
            List<Demo> demoList = this.demoBusiness.selectDemoList(param);
            if (CollectionUtils.isEmpty(demoList) || !demoList.get(0).getShortcol().equals((short) 2)) {
                Assert.fail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void testUpdate() {
        DemoParam param = new DemoParam();
        param.createCriteria().andBytecolEqualTo((byte) 1);
        try {
            List<Demo> demoList = this.demoBusiness.selectDemoList(param);

            if (CollectionUtils.isEmpty(demoList) || !demoList.get(0).getShortcol().equals((short) 2)) {
                Assert.fail();
            }

            long key = demoList.get(0).getId();

            Demo newDemo = new Demo();
            newDemo.setId(key);
            String newColumn1 = "B";
            String newColumn2 = "String column2 new";
            newDemo.setStringcol1(newColumn1);
            newDemo.setStringcol2(newColumn2);


            int count = this.demoBusiness.update(newDemo);
            if (count != 1) {
                Assert.fail();
            }

            // reload data
            Demo afterDemo = this.demoBusiness.getDemoByKey(key);
            if (afterDemo == null || !newColumn1.equals(afterDemo.getStringcol1())
                    || !newColumn2.equals(afterDemo.getStringcol2())) {
                Assert.fail();
            }
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
