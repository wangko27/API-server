package io.nuls.api.context;

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
    private static List<UtxoDto> blockDtos = new ArrayList<>();
    public static void add(UtxoDto block){
        blockDtos.add(block);
    }

    public static List<UtxoDto> getAllUtxoDtos(){
        return blockDtos;
    }

    public static List<UtxoDto> getAll(){
        Collections.sort(blockDtos, new Comparator<UtxoDto>() {
            @Override
            public int compare(UtxoDto o1, UtxoDto o2) {
                return o2.getTotal().compareTo(o1.getTotal());
            }
        });
        return blockDtos;
    }
    public static void reset(List<UtxoDto> list){
        blockDtos = list;
    }
    public static int getSize(){
        return blockDtos.size();
    }
}
