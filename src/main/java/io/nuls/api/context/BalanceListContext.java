package io.nuls.api.context;

import io.nuls.api.server.dto.BlockDto;
import io.nuls.api.server.dto.UtxoDto;

import java.util.ArrayList;
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

    public static List<UtxoDto> getAll(){
        return blockDtos;
    }

    public static void clear(){
        blockDtos.clear();
    }
    public static void reset(List<UtxoDto> list){
        clear();
        blockDtos = list;
        for(UtxoDto dto:blockDtos){
            System.out.println(dto.getAddress());
            System.out.println(dto.getTotal());
            System.out.println("-----------");
        }
    }
}
