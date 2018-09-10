package io.nuls.api.server;

import io.nuls.api.constant.Constant;
import io.nuls.api.server.dao.util.EhcacheUtil;
import org.junit.Test;

/**
 * Description:
 * Author: moon
 * Date:  2018/8/24 0024
 */
public class EcacheTest {

    @Test
    public void Test() throws InterruptedException {
        EhcacheUtil.getInstance().putWithTime(Constant.UTXO_CACHE_NAME, "xxxJson", "aaa",5);
        System.out.println("save success，get：");
        System.out.println(EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME, "xxxJson"));
        Thread.sleep(1000);
        System.out.println("------------");
        EhcacheUtil.getInstance().putWithTime(Constant.UTXO_CACHE_NAME, "xxxJson", "aabba",5);
        System.out.println("save success，get：");
        System.out.println(EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME, "xxxJson"));
        Thread.sleep(10000);
        System.out.println("sleep after,get:");
        Object obj = EhcacheUtil.getInstance().get(Constant.UTXO_CACHE_NAME, "xxxJson");
        System.out.println(obj);

    }



}
