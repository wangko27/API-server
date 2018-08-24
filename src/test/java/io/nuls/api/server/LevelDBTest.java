package io.nuls.api.server;

import io.nuls.api.entity.Utxo;
import io.nuls.api.model.Result;
import io.nuls.api.server.leveldb.service.BatchOperation;
import io.nuls.api.server.leveldb.service.DBService;
import io.nuls.api.server.leveldb.service.impl.LevelDBServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class LevelDBTest {

    private static DBService dbService;
    private static String areaName = "utxoCache";

    @BeforeClass
    public static void init() {
        dbService = new LevelDBServiceImpl();
        dbService.createArea(areaName);

    }

    @Test
    public void testUtxo() {
        String key = "0020942a4c3d6eed38f28fd8f34322069b0c642d0140c6e46d52f74c3c49dd8ea6e1_1";
        Utxo utxo = dbService.getModel(areaName, key.getBytes(), Utxo.class);
        System.out.println(utxo.getSpendTxHash());
    }

    @Test
    public void testPut() {
        Utxo utxo = new Utxo();
        utxo.setHashIndex("testHash123_1");
        utxo.setAddress("testAddress321");
        utxo.setAmount(12345678L);
        utxo.setLockTime(2345678976L);
        utxo.setSpendTxHash("testSpendHash122");

        String key = "testHash123" + "-" + 1;      //  txhash-txIndex
        Result<Utxo> result = dbService.putModel(areaName, key.getBytes(), utxo);

        System.out.println(result.getMsg());
    }

    @Test
    public void testGet() {
        String key = "testHash123" + "-" + 1;
        Utxo utxo = dbService.getModel(areaName, key.getBytes(), Utxo.class);
        System.out.println(utxo.getSpendTxHash());
    }

    @Test
    public void testRemove() {
        String key = "testHash123" + "-" + 1;
        dbService.delete(areaName, key.getBytes());

        Utxo utxo = dbService.getModel(areaName, key.getBytes(), Utxo.class);
        System.out.println(utxo == null);
    }

    @Test
    public void testAddList() {
        BatchOperation batch = dbService.createWriteBatch(areaName);

        Utxo utxo = new Utxo();
        utxo.setHashIndex("testHash123_1");
        utxo.setAddress("testAddress111");
        utxo.setAmount(111111111L);
        utxo.setLockTime(1234556789L);
        utxo.setSpendTxHash("testSpendHash111");
        batch.putModel(utxo.getHashIndex().getBytes(), utxo);

        Utxo utxo2 = new Utxo();
        utxo.setHashIndex("testHash123_1");
        utxo2.setAddress("testAddress222");
        utxo2.setAmount(222222222L);
        utxo2.setLockTime(987654321L);
        utxo2.setSpendTxHash("testSpendHash222");
        batch.putModel(utxo2.getHashIndex().getBytes(), utxo2);
        batch.executeBatch();
    }

    @Test
    public void testAddMoreList() {
        BatchOperation batch = dbService.createWriteBatch(areaName);

        Utxo utxo = new Utxo();
        utxo.setHashIndex("testHash123_1");
        utxo.setAddress("testAddress333");
        utxo.setAmount(33333333L);
        utxo.setLockTime(1234556789L);
        utxo.setSpendTxHash("testSpendHash333");
        batch.putModel(utxo.getHashIndex().getBytes(), utxo);

        Utxo utxo2 = new Utxo();
        utxo.setHashIndex("testHash123_1");
        utxo2.setAddress("testAddress444");
        utxo2.setAmount(44444444L);
        utxo2.setLockTime(987654321L);
        utxo2.setSpendTxHash("testSpendHash444");
        batch.putModel(utxo2.getHashIndex().getBytes(), utxo2);
        batch.executeBatch();
    }

    @Test
    public void testGetList() {
        List<Utxo> list = dbService.values(areaName, Utxo.class);
        System.out.println(list.size());
        Utxo utxo1 = list.get(1);
        System.out.println(utxo1.getHashIndex());
    }

    @Test
    public void destroyArea() {
        dbService.destroyArea(areaName);
    }
}
