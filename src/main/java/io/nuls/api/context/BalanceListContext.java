package io.nuls.api.context;

import io.nuls.api.constant.Constant;
import io.nuls.api.server.dao.mapper.leveldb.BalanceLevelDbService;
import io.nuls.api.server.dto.UtxoDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description: 持币账户静态统计
 * Author: zsj
 * Date:  2018/6/5 0005
 */
public class BalanceListContext {

    private static BalanceLevelDbService balanceLevelDbService = BalanceLevelDbService.getInstance();

    private static List<UtxoDto> blockDtos = null;
    public static void add(UtxoDto block){
        blockDtos.add(block);
    }

    public static List<UtxoDto> getAllUtxoDtos(){
        if(null == blockDtos){
            blockDtos = balanceLevelDbService.select(Constant.BALANCE_DB_NAME);
        }
        return blockDtos;
    }

    public static List<UtxoDto> getAll(){
        Collections.sort(getAllUtxoDtos(), new Comparator<UtxoDto>() {
            @Override
            public int compare(UtxoDto o1, UtxoDto o2) {
                return o2.getTotal().compareTo(o1.getTotal());
            }
        });
        return blockDtos;
    }
    public static void reset(List<UtxoDto> list){
        if(null != list){
            balanceLevelDbService.insert(Constant.BALANCE_DB_NAME,list);
        }
        blockDtos = list;
    }
    public static int getSize(){
        if(null != blockDtos){
            return blockDtos.size();
        }
        return 0;
    }
}
