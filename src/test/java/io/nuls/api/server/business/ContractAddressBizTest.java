package io.nuls.api.server.business;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.ContrackAddressInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ApplicationContext.xml")
public class ContractAddressBizTest {

    @Autowired
    private ContrackAddressBusiness contrackAddressBusiness;

    @Test
    public void insertContractAddress() {
        ContrackAddressInfo address = new ContrackAddressInfo();

        address.setContractAddress("NseDNHmwumcbCxMiLF5QXZz6paJjG2Rz");
        address.setCreater("NsdvCLtZZBX4QHkZcvkyrRfoeJjRorRW");
        address.setCreateTxHash("0020508318f23803986bf8f075cfc3f3e75b81e06212c0f6fa70caad7f88e56e99a7");
        address.setBlockHeight(100L);
        address.setIsNrc20(1);
        address.setStatus(1);
        address.setCreateTime(System.currentTimeMillis());
        contrackAddressBusiness.save(address);

    }

    @Test
    public void getBlock() {
        String hash = "aaaaa";
        System.out.println(hash);
    }



//    @Test
//    public void getBlockList() {
//        long begin = 99;
//        long end = 100;
//        List<BlockHeader> blockHeaderList = blockBusiness.getBlockList(begin,end);
//
//    }


}
