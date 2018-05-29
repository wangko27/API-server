package io.nuls.api.server;

import io.nuls.api.entity.BlockHeader;
import io.nuls.api.server.business.BlockBusiness;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Scanner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:ApplicationContext.xml")
public class MybatisTest {

    @Autowired
    BlockBusiness blockBusiness;

    @Test
    public void insertBlock() {
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setHash("bbbbb");
        blockHeader.setConsensusAddress("sdfghjkl");
        blockHeader.setCreateTime(System.currentTimeMillis());
        blockHeader.setHeight(100L);
        blockHeader.setMerkleHash("bbbbb");
        blockHeader.setPrevHash("00000");
        blockHeader.setPackingIndexOfRound(100);
        blockHeader.setReward(999L);
        blockHeader.setRoundStartTime(System.currentTimeMillis());
        blockHeader.setSize(333);
        blockHeader.setTxCount(20);
        blockHeader.setTotalFee(3445L);
        blockHeader.setExtend(new byte[]{1,2,'a','b'});
        blockHeader.setRoundIndex(7);
        blockBusiness.saveBlock(blockHeader);
    }

    @Test
    public void getBlock() {
        String hash = "aaaaa";
        BlockHeader blockHeader = blockBusiness.getBlockByHash(hash);
    }

    @Test
    public void getBlockByHeight() {
        long height = 100;
        BlockHeader blockHeader = blockBusiness.getBlockByHeight(height);
    }

    @Test
    public void getBlockList() {
        long begin = 99;
        long end = 100;
        List<BlockHeader> blockHeaderList = blockBusiness.getBlockList(begin,end);

    }

    @Test
    public void testTask() {
        Scanner scan = new Scanner(System.in);
        scan.next();
    }
}
