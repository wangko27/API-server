package io.nuls.api.server;

import com.github.pagehelper.PageInfo;
import io.nuls.api.entity.BlockHeader;
import io.nuls.api.entity.Transaction;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.TransactionBusiness;
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
    private BlockBusiness blockBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;

    @Test
    public void insertBlock() {
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setHash("bbbbb");
        blockHeader.setConsensusAddress("sdfghjkl");
        blockHeader.setCreateTime(System.currentTimeMillis());
        blockHeader.setHeight(100L);
        blockHeader.setMerkleHash("bbbbb");
        blockHeader.setPreHash("00000");
        blockHeader.setPackingIndexOfRound(100);
        blockHeader.setReward(999L);
        blockHeader.setRoundStartTime(System.currentTimeMillis());
        blockHeader.setSize(333);
        blockHeader.setTxCount(20);
        blockHeader.setTotalFee(3445L);
        blockHeader.setExtend(new byte[]{1,2,'a','b'});
        blockHeader.setRoundIndex(7L);
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
    public void getBlockPage() {
        int pageNumber = 1;
        int pageSize = 10;
        PageInfo<BlockHeader> pageInfo = blockBusiness.getBlockPage(1, 10);
        System.out.println(pageInfo.getPages());
        System.out.println(pageInfo.getTotal());
    }

    @Test
    public void testTask() {
        Scanner scan = new Scanner(System.in);
        scan.next();
    }

    @Test
    public void testTx() throws Exception {
        String hash = "0020197be4df20c0f405d611303efc1dedd952d0b2f224e3dd8cb461c1a800a99f50";
        Transaction tx = transactionBusiness.getByKey(hash);
        tx.transferExtend();
    }
}
