package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.EntityConstant;
import io.nuls.api.constant.ErrorCode;
import io.nuls.api.context.IndexContext;
import io.nuls.api.crypto.Hex;
import io.nuls.api.entity.*;
import io.nuls.api.model.tx.AliasTransaction;
import io.nuls.api.model.tx.CancelDepositTransaction;
import io.nuls.api.model.tx.DepositTransaction;
import io.nuls.api.model.tx.TransferTransaction;
import io.nuls.api.server.business.BlockBusiness;
import io.nuls.api.server.business.TransactionBusiness;
import io.nuls.api.server.business.UtxoBusiness;
import io.nuls.api.server.business.WebwalletTransactionBusiness;
import io.nuls.api.server.dto.TransactionDto;
import io.nuls.api.server.dto.TransactionParam;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.NulsByteBuffer;
import io.nuls.api.utils.RpcTransferUtil;
import io.nuls.api.utils.StringUtils;
import io.nuls.api.utils.TransactionTool;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

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
    @Autowired
    private SyncDataHandler syncDataHandler;
    @Autowired
    private WebwalletTransactionBusiness webwalletTransactionBusiness;

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
    public RpcClientResult list(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("address")String address,@QueryParam("type")int type,@QueryParam("startTime")long startTime,@QueryParam("endTime")long endTime){
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
        result.setData(transactionBusiness.getListByAddress(address,type,startTime,endTime,pageNumber,pageSize));
        return result;
    }

    @GET
    @Path("/list/webwallet/address")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult listWebWallet(@QueryParam("address")String address,@QueryParam("status") int status,@QueryParam("type") int type,@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize){
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
        result.setData(webwalletTransactionBusiness.getAll(address,EntityConstant.WEBWALLET_STATUS_NOTCONFIRM,type,pageNumber,pageSize));
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
    public RpcClientResult getTransFee(@QueryParam("address")String address,@QueryParam("money")long money,@QueryParam("remark")String remark,@QueryParam("price")long price,@QueryParam("types")int types,@QueryParam("alias")String alias){
        RpcClientResult result = null;
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed(ErrorCode.ADDRESS_ERROR);
        }
        if(money <= 0){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if(types <= 0){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        result = RpcClientResult.getSuccess();
        Map<String,String> attr = new HashMap<String,String>();
        List<Utxo> list = utxoBusiness.getUsableUtxo(address);
        io.nuls.api.model.Na na  = null;
        if(types == EntityConstant.TX_TYPE_TRANSFER){
            //转账
            na = TransactionTool.getTransferTxFee(list,money,remark,price);
        }else if(types == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
            //设置别名
            na = TransactionTool.getAliasTxFee(list,address,alias);
        }else if(types == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
            //加入共识
            na = TransactionTool.getJoinAgentTxFee(list,money);
        }else if(types == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
            //退出共识
            na = io.nuls.api.model.Na.CENT;
        }else{
            //其他，暂时不处理
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if(null == na){
            return RpcClientResult.getFailed(ErrorCode.BALANCE_NOT_ENOUGH);
        }
        //io.nuls.sdk.core.utils.TransactionTool.signTransaction();
        attr.put("fee",na.getValue()+"");
        result.setData(attr);
        return result;
    }

    @POST
    @Path("/trans")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult trans(TransactionParam transactionParam) throws IOException {
        RpcClientResult result;
        result = RpcClientResult.getSuccess();
        Map<String,Object> attr = new HashMap<>();
        attr.put("hash","");
        attr.put("seri", "");
        if(null == transactionParam){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        List<Utxo> list = utxoBusiness.getUsableUtxo(transactionParam.getAddress());
        io.nuls.api.model.Na na  = null;
        if(transactionParam.getTypes() == EntityConstant.TX_TYPE_TRANSFER){
            //转账
            na = TransactionTool.getTransferTxFee(list,transactionParam.getMoney(),transactionParam.getRemark(),transactionParam.getPrice());
            if(null == na){
                return RpcClientResult.getFailed(ErrorCode.BALANCE_NOT_ENOUGH);
            }
            //组装交易
            TransferTransaction transferTransaction = TransactionTool.createTransferTx(list,transactionParam.getToAddress(),transactionParam.getMoney(),transactionParam.getRemark(),na.getValue());
            attr.put("hash",transferTransaction.getHash().getDigestHex());
            attr.put("seri", Base64.getEncoder().encodeToString(transferTransaction.serialize()));
        }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
            //设置别名
            na = TransactionTool.getAliasTxFee(list,transactionParam.getAddress(),transactionParam.getAlias());
            if(null == na){
                return RpcClientResult.getFailed(ErrorCode.BALANCE_NOT_ENOUGH);
            }
            //组装交易
            AliasTransaction aliasTransaction = TransactionTool.createAliasTx(list,transactionParam.getAddress(),transactionParam.getAlias(),na.getValue());
            attr.put("hash",aliasTransaction.getHash().getDigestHex());
            attr.put("seri", Base64.getEncoder().encodeToString(aliasTransaction.serialize()));
        }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
            //加入共识
            na = TransactionTool.getJoinAgentTxFee(list,transactionParam.getMoney());
            if(null == na){
                return RpcClientResult.getFailed(ErrorCode.BALANCE_NOT_ENOUGH);
            }
            //组装交易
            DepositTransaction depositTransaction = TransactionTool.createDepositTx(list,transactionParam.getAddress(),transactionParam.getAgentHash(),transactionParam.getMoney(),na.getValue());
            attr.put("hash",depositTransaction.getHash().getDigestHex());
            attr.put("seri", Base64.getEncoder().encodeToString(depositTransaction.serialize()));
        }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
            //退出共识
            na = io.nuls.api.model.Na.CENT;
            if(null == na){
                return RpcClientResult.getFailed(ErrorCode.BALANCE_NOT_ENOUGH);
            }
            //组装交易
            Transaction tx = transactionBusiness.getByHash(transactionParam.getAgentHash());
            if(null != tx.getOutputList()){
                for(Output output:tx.getOutputList()){
                    Utxo utxo = utxoBusiness.getByKey(output.getKey());
                    if(utxo.getLockTime() == -1){
                        CancelDepositTransaction cancelDepositTransaction = TransactionTool.createCancelDepositTx(utxo);
                        attr.put("hash",cancelDepositTransaction.getHash().getDigestHex());
                        attr.put("seri", Base64.getEncoder().encodeToString(cancelDepositTransaction.serialize()));
                    }
                }
            }
        }else{
            //其他，暂时不处理
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        result.setData(attr);
        return result;
    }

    @POST
    @Path("/broadcast")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult broadcast(TransactionParam transactionParam) throws Exception {
        RpcClientResult result;
        if(StringUtils.isBlank(transactionParam.getSerializ())){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if(StringUtils.isBlank(transactionParam.getSign())){
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
        if(transactionParam.getTypes() == EntityConstant.TX_TYPE_TRANSFER){
            //转账
            byte[] data = Base64.getDecoder().decode(transactionParam.getSerializ());
            TransferTransaction transaction = new TransferTransaction();
            transaction.parse(new NulsByteBuffer(data));
            transaction.setScriptSig(Hex.decode(transactionParam.getSign()));
            Map<String, String> params = new HashMap<>();
            params.put("txHex", Hex.encode(transaction.serialize()));
            System.out.println("time1:"+transaction.getTime());
            return broadCaseSave(params,transaction);

        }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
            //设置别名
            byte[] data = Base64.getDecoder().decode(transactionParam.getSerializ());
            AliasTransaction transaction = new AliasTransaction();
            transaction.parse(new NulsByteBuffer(data));
            transaction.setScriptSig(Hex.decode(transactionParam.getSign()));
            Map<String, String> params = new HashMap<>();
            params.put("txHex", Hex.encode(transaction.serialize()));
            System.out.println("time2:"+transaction.getTime());
            return broadCaseSave(params,transaction);
        }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
            //加入共识
            byte[] data = Base64.getDecoder().decode(transactionParam.getSerializ());
            DepositTransaction transaction = new DepositTransaction();
            transaction.parse(new NulsByteBuffer(data));
            transaction.setScriptSig(Hex.decode(transactionParam.getSign()));
            Map<String, String> params = new HashMap<>();
            params.put("txHex", Hex.encode(transaction.serialize()));
            System.out.println("time3:"+transaction.getTime());
            return broadCaseSave(params,transaction);
        }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
            //退出共识
            byte[] data = Base64.getDecoder().decode(transactionParam.getSerializ());
            CancelDepositTransaction transaction = new CancelDepositTransaction();
            transaction.parse(new NulsByteBuffer(data));
            transaction.setScriptSig(Hex.decode(transactionParam.getSign()));
            Map<String, String> params = new HashMap<>();
            params.put("txHex", Hex.encode(transaction.serialize()));
            System.out.println("time4:"+transaction.getTime());
            return broadCaseSave(params,transaction);
        }else{
            //其他，暂时不处理
            return RpcClientResult.getFailed(ErrorCode.PARAMETER_ERROR);
        }
    }

    private RpcClientResult broadCaseSave(Map<String, String> params,io.nuls.api.model.Transaction transaction) throws Exception {
        RpcClientResult result = syncDataHandler.broadcast(params);
        if(result.isSuccess()){
            Transaction tx = RpcTransferUtil.toTransaction(transaction);
            //保存交易和新生成的Utxo，同时，删除掉已经花费的utxo
            Utxo utxoKey = utxoBusiness.getByKey(tx.getInputs().get(0).getKey());
            WebwalletTransaction webwalletTransaction = new WebwalletTransaction(tx,params.get("txHex"),utxoKey.getAddress());
            if(webwalletTransactionBusiness.save(webwalletTransaction,utxoKey.getAddress()) > 0){
                return result;
            }
        }else{
            return result;
        }
        return result;
    }
}
