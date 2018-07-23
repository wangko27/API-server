package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.ErrorCode;
import io.nuls.api.context.IndexContext;
import io.nuls.api.entity.*;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.TransactionBusiness;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.dto.TransactionDto;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.log.Log;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: zsj
 * Date:  2018/5/29 0029
 */
@Path("tx")
@Component
public class TransactionResource {

    @Autowired
    private UtxoBusiness utxoBusiness;
    @Autowired
    private BlockBusiness blockBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;

    @GET
    @Path("/index")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult index(){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(IndexContext.getTransactions());
        return result;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult list(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("height")Long height,@QueryParam("type")int type){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            return result;
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        result = RpcClientResult.getSuccess();
        //全部交易，默认时间排序
        result.setData(transactionBusiness.getList(height,type,pageNumber,pageSize,3));
        return result;
    }

    @GET
    @Path("/list/address")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult list(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("address")String address,@QueryParam("type")int type){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
            return result;
        }
        if (pageNumber == 0) {
            pageNumber = 1;
        }
        if (pageSize == 0) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        if(!StringUtils.validAddress(address)){
            result = RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
            return result;
        }
        result = RpcClientResult.getSuccess();
        result.setData(transactionBusiness.getListByAddress(address,type,pageNumber,pageSize));
        return result;
    }

    @GET
    @Path("/hash/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getDetailByHash(@PathParam("hash") String hash){
        RpcClientResult result;
        if (!StringUtils.validHash(hash)) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RpcClientResult.getSuccess();
            Transaction transaction = transactionBusiness.getByHash(hash);
            if(null == transaction){
                return RpcClientResult.getFailed(ErrorCode.DATA_NOT_FOUND);
            }
            List<Utxo> outputs = new ArrayList<>();
            if(null != transaction.getOutputList()){
                for(Output output:transaction.getOutputList()){
                    outputs.add(utxoBusiness.getByKey(output.getKey()));
                }
            }
            transaction.setOutputs(outputs);
            //处理格式，null等错误
            transactionBusiness.formatTransForDetail(transaction);
            TransactionDto transactionDto = new TransactionDto(transaction);
            BlockHeader blockHeader = blockBusiness.getNewest();
            if(null != blockHeader){
                Long height = blockHeader.getHeight();
                if(null != height && null != transaction.getBlockHeight()){
                    transactionDto.setConfirmCount(height-transaction.getBlockHeight());
                }
            }
            result.setData(transactionDto);
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/hash/spent")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getDetailBySpentHash(@QueryParam("hash") String hash,@QueryParam("index") Integer index){
        RpcClientResult result;
        if (!StringUtils.validHash(hash)) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if (null == index || index < 0) {
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RpcClientResult.getSuccess();
            result.setData(utxoBusiness.getUtxoBySpentHash(hash,index));
        } catch (Exception e) {
            result = RpcClientResult.getFailed();
            Log.error(e);
        }
        return result;
    }

    @GET
    @Path("/transFee")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getTransFee(@QueryParam("address")String address,@QueryParam("money")long money,@QueryParam("remark")long remark,@QueryParam("price")long price){
        int size = 0;
        RpcClientResult result = null;
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
        }
        if(money <= 0){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        result = RpcClientResult.getSuccess();
        Map<String,String> attr = new HashMap<String,String>();
        List<Utxo> list = utxoBusiness.getUsableUtxo(address);
        List<Utxo> useable = new ArrayList<>();
        long usable = 0;
        int round = 0;
        for(int i =0; i< list.size(); i++){
            Utxo utxo = list.get(i);
            round = i;
            usable += utxo.getAmount();
            if(usable > money){
                useable.add(utxo);
                break;
            }
        }
        if(usable > money){
            //todo 计算手续费，返回前端
            // (124+50*inputs.length+38*outputs.length + remark.bytes.length)/1024
            //long base = 124+50*inputs.length+38*outputs.length + remark.bytes.length;
            long k =(124+(50*useable.size())+38)/1024;

            for(int i = round; i<list.size(); i++){

            }
        }else{
            return RpcClientResult.getFailed(ErrorCode.BALANCE_NOT_ENOUGH);
        }
        result.setData(attr);
        return result;
    }

    @POST
    @Path("/trans")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult trans(Alias alias){
        RpcClientResult result;
        result = RpcClientResult.getSuccess();
        result.setData(alias);
        return result;
    }




}
