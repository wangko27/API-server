package io.nuls.api.server.resources.impl;

import io.nuls.api.constant.Constant;
import io.nuls.api.constant.EntityConstant;
import io.nuls.api.constant.KernelErrorCode;
import io.nuls.api.crypto.Hex;
import io.nuls.api.entity.*;
import io.nuls.api.exception.NulsException;
import io.nuls.api.model.tx.*;
import io.nuls.api.server.business.*;
import io.nuls.api.server.dao.mapper.leveldb.WebwalletUtxoLevelDbService;
import io.nuls.api.server.dto.ContractTransParam;
import io.nuls.api.server.dto.TransFeeDto;
import io.nuls.api.server.dto.TransactionDto;
import io.nuls.api.server.dto.TransactionParam;
import io.nuls.api.server.resources.SyncDataHandler;
import io.nuls.api.utils.*;
import io.nuls.api.utils.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Description:
 * Author: moon
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
    @Autowired
    private AliasBusiness aliasBusiness;

    private WebwalletUtxoLevelDbService webwalletUtxoLevelDbService = WebwalletUtxoLevelDbService.getInstance();

    @GET
    @Path("/index")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult index(){
        RpcClientResult result = RpcClientResult.getSuccess();
        result.setData(transactionBusiness.getIndexTransaction());
        return result;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult list(@QueryParam("pageNumber") int pageNumber, @QueryParam("pageSize") int pageSize,@QueryParam("height")Long height,@QueryParam("type")int type){
        RpcClientResult result = null;
        if (pageNumber < 0 || pageSize < 0) {
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
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
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
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
            result = RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
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
            result = RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
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
            result = RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
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
            return RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
        }
        try {
            result = RpcClientResult.getSuccess();
            Transaction transaction = transactionBusiness.getByHash(hash);
            if(null == transaction){
                return RpcClientResult.getFailed(KernelErrorCode.DATA_NOT_FOUND);
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
    @Path("/transFee")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult getTransFee(@QueryParam("address")String address,@QueryParam("money")long money,@QueryParam("remark")String remark,@QueryParam("price")long price,@QueryParam("types")int types,@QueryParam("alias")String alias){
        RpcClientResult result;
        if(!StringUtils.validAddress(address)){
            return RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
        }
        result = valiHeight();
        if(result.isSuccess()){
            result = RpcClientResult.getSuccess();
            Map<String,String> attr = new HashMap<String,String>(3);
            int feeType = EntityConstant.TRANSFEE_NOTENOUGHT_OK;
            List<Utxo> list = utxoBusiness.getUsableUtxo(address);
            TransFeeDto transFeeDto = null;
            if(types == EntityConstant.TX_TYPE_TRANSFER){
                if(money <= 0){
                    return RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
                }
                if (!StringUtils.validTxRemark(remark)) {
                    return RpcClientResult.getFailed(KernelErrorCode.TX_REMARK_LENTH_ERROR);
                }
                //size: 备注长度+162
                transFeeDto = TransactionTool.getTransferTxFee(list,money,remark,price);
                if(null == transFeeDto){
                    //余额不足，计算最大可以转多少
                    feeType = EntityConstant.TRANSFEE_NOTENOUGHT_UTXO;
                    transFeeDto = TransactionTool.calcMaxFee(list,TransactionTool.getRemarkSize(remark)+162,price);
                }
            }else if(types == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
                //设置别名
                //size: 162
                transFeeDto = TransactionTool.getAliasTxFee(list,address,alias);
            }else if(types == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
                //加入共识
                //size: 288
                if(money < Constant.ENTRUSTER_DEPOSIT_LOWER_LIMIT.getValue()){
                    return RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
                }
                transFeeDto = TransactionTool.getJoinAgentTxFee(list,money);
                if(null == transFeeDto){
                    //余额不足，计算最大可以共识多少
                    feeType = EntityConstant.TRANSFEE_NOTENOUGHT_UTXO;
                    transFeeDto = TransactionTool.calcMaxFee(list,288,price);
                }
            }else if(types == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
                //退出共识
                transFeeDto = new TransFeeDto();
                //1kb
                transFeeDto.setSize(1024);
                transFeeDto.setNa(io.nuls.api.model.Na.CENT);
            }else{
                //其他，暂时不处理
                return RpcClientResult.getFailed(KernelErrorCode.TX_TYPE_NULL);
            }
            if(null == transFeeDto){
                return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
            }
            if(transFeeDto.getSize() > TransactionFeeCalculator.MAX_TX_SIZE){
                return RpcClientResult.getFailed(KernelErrorCode.BALANCE_TOO_MUCH);
            }
            attr.put("fee",transFeeDto.getNa().getValue()+"");
            attr.put("feeType",feeType+"");
            attr.put("feeSize",transFeeDto.getSize()+"");
            result.setData(attr);
        }
        return result;
    }

    @POST
    @Path("/trans")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult trans(TransactionParam transactionParam) throws Exception {
        RpcClientResult result;
        Map<String,Object> attr = new HashMap<>(1);
        attr.put("hash","");
        if(null == transactionParam){
            return RpcClientResult.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        if(StringUtils.isBlank(transactionParam.getAddress())){
            return RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
        }
        if(transactionParam.getTypes() <= 0){
            return RpcClientResult.getFailed(KernelErrorCode.TX_TYPE_NULL);
        }
        result = valiHeight();
        if(result.isSuccess()){
            List<Utxo> list = utxoBusiness.getUsableUtxo(transactionParam.getAddress());
            io.nuls.api.model.Na na  = null;
            io.nuls.api.model.Transaction transaction = null;
            String temp = "";
            TransFeeDto transFeeDto = null;
            if(transactionParam.getTypes() == EntityConstant.TX_TYPE_TRANSFER){
                //金额不正确
                if(transactionParam.getMoney()<=0){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_MONEY_NULL);
                }
                //手续费不正确
                if(transactionParam.getPrice()<TransactionFeeCalculator.MIN_PRECE_PRE_1024_BYTES.getValue() || transactionParam.getPrice() > TransactionFeeCalculator.OTHER_PRECE_PRE_1024_BYTES.getValue()){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_PRICE_NULL);
                }
                //转入地址不正确
                if(!StringUtils.validAddress(transactionParam.getToAddress())){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_TOADDRESS_NULL);
                }
                if(StringUtils.isNotBlank(transactionParam.getRemark()) && StringUtils.isSpecialChar(transactionParam.getRemark())){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_REMARK_ERROR);
                }
                if (!StringUtils.validTxRemark(transactionParam.getRemark())) {
                    return RpcClientResult.getFailed(KernelErrorCode.TX_REMARK_LENTH_ERROR);
                }
                //转账
                transFeeDto =TransactionTool.getTransferTxFee(list,transactionParam.getMoney(),transactionParam.getRemark(),transactionParam.getPrice());
                if(null == transFeeDto){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
                }
                na = transFeeDto.getNa();
                if(null == na){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
                }
                if(transFeeDto.getSize() > TransactionFeeCalculator.MAX_TX_SIZE){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_TOO_MUCH);
                }
                if(transFeeDto.getSize() > TransactionFeeCalculator.MAX_TX_SIZE){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_TOO_MUCH);
                }
                //组装交易
                transaction = TransactionTool.createTransferTx(list,transactionParam.getToAddress(),transactionParam.getMoney(),transactionParam.getRemark(),na.getValue());
            }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
                //设置别名
                //别名格式不正确
                if(StringUtils.isBlank(transactionParam.getAlias()) || !StringUtils.valiAlias(transactionParam.getAlias())){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_ALIAS_ERROR);
                }
                //别名长度不正确
                if(transactionParam.getAlias().length() > 20){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_ALIAS_ERROR);
                }
                //别名已存在
                if(webwalletTransactionBusiness.getAliasByAlias(transactionParam.getAlias()) > 0
                        || null!=aliasBusiness.getAliasByAlias(transactionParam.getAlias())){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_ALIAS_USED_ERROR);
                }
                //该用户已经设置过别名了
                if(null!=aliasBusiness.getAliasByAddress(transactionParam.getAddress())
                        || webwalletTransactionBusiness.getAliasByAddress(transactionParam.getAddress()) > 0){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_ALIAS_USED_ERROR);
                }
                transFeeDto = TransactionTool.getAliasTxFee(list,transactionParam.getAddress(),transactionParam.getAlias());
                if(null == transFeeDto){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
                }
                na = transFeeDto.getNa();
                if(null == na){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
                }
                if(transFeeDto.getSize() > TransactionFeeCalculator.MAX_TX_SIZE){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_TOO_MUCH);
                }
                temp = transactionParam.getAlias();
                //组装交易
                transaction = TransactionTool.createAliasTx(list,transactionParam.getAddress(),transactionParam.getAlias(),na.getValue());
            }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
                //金额不正确
                if(transactionParam.getMoney()<=0){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_MONEY_NULL);
                }
                //委托的节点hash不正确
                if(!StringUtils.validHash(transactionParam.getAgentHash())){
                    return RpcClientResult.getFailed(KernelErrorCode.HASH_ERROR);
                }
                //加入共识
                transFeeDto = TransactionTool.getJoinAgentTxFee(list,transactionParam.getMoney());
                if(null == transFeeDto){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
                }
                na = transFeeDto.getNa();
                if(null == na){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
                }
                if(transFeeDto.getSize() > TransactionFeeCalculator.MAX_TX_SIZE){
                    return RpcClientResult.getFailed(KernelErrorCode.BALANCE_TOO_MUCH);
                }
                //组装交易
                transaction = TransactionTool.createDepositTx(list,transactionParam.getAddress(),transactionParam.getAgentHash(),transactionParam.getMoney(),na.getValue());
            }else if(transactionParam.getTypes() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
                //退出委托的hash不正确
                if(!StringUtils.validHash(transactionParam.getAgentHash())){
                    return RpcClientResult.getFailed(KernelErrorCode.HASH_ERROR);
                }
                if(webwalletTransactionBusiness.getDepositCoutByAddressAgentHash(transactionParam.getAgentHash()) > 0){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_ALIAS_CANCEL_DEPOSIT_ERROR);
                }
                temp = transactionParam.getAgentHash();
                //组装交易
                Transaction tx = transactionBusiness.getByHash(transactionParam.getAgentHash());
                if(null != tx.getOutputList()){
                    for(Output output:tx.getOutputList()){
                        Utxo utxo = utxoBusiness.getByKey(output.getKey());
                        if(utxo.getLockTime() == -1){
                            transaction = TransactionTool.createCancelDepositTx(utxo);
                            break;
                        }
                    }
                }
            }else{
                //其他，暂时不处理
                return RpcClientResult.getFailed(KernelErrorCode.TX_TYPE_NULL);
            }
            if(null != transaction){
                attr.put("hash",transaction.getHash().getDigestHex());
                attr.put("tx",transaction);
                Transaction tx = RpcTransferUtil.toTransaction(transaction);
                Utxo utxoKey = null;
                for(Input input:tx.getInputs()){
                    utxoKey = utxoBusiness.getByKey(input.getKey());
                    if(utxoKey == null){
                        utxoKey = webwalletUtxoLevelDbService.select(transactionParam.getAddress());
                    }
                    input.setAddress(utxoKey.getAddress());
                    input.setValue(utxoKey.getAmount());
                }
                WebwalletTransaction webwalletTransaction = new WebwalletTransaction(tx,Base64.getEncoder().encodeToString(transaction.serialize()),transactionParam.getAddress(),temp);
                if(webwalletTransactionBusiness.save(webwalletTransaction) != 1){
                    return RpcClientResult.getFailed(KernelErrorCode.TX_SAVETEMPUTXO_ERROR);
                }
            }else{
                //为空，说明有系统异常
                return RpcClientResult.getFailed(KernelErrorCode.FAILED);
            }
            result.setData(attr);
        }
        return result;
    }

    @POST
    @Path("/contractTrans")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult contractTrans(ContractTransParam contractTransParam) throws Exception {
        if(null == contractTransParam){
            return RpcClientResult.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        if(StringUtils.isBlank(contractTransParam.getSender())){
            return RpcClientResult.getFailed(KernelErrorCode.ADDRESS_ERROR);
        }
        if(contractTransParam.getTypes() <= 0){
            return RpcClientResult.getFailed(KernelErrorCode.TX_TYPE_NULL);
        }
        //手续费不正确
        if(contractTransParam.getPrice()<0){
            return RpcClientResult.getFailed(KernelErrorCode.TX_PRICE_NULL);
        }

        if(StringUtils.isNotBlank(contractTransParam.getRemark()) && StringUtils.isSpecialChar(contractTransParam.getRemark())){
            return RpcClientResult.getFailed(KernelErrorCode.TX_REMARK_ERROR);
        }
        if (!StringUtils.validTxRemark(contractTransParam.getRemark())) {
            return RpcClientResult.getFailed(KernelErrorCode.TX_REMARK_LENTH_ERROR);
        }
        RpcClientResult result  = valiHeight();
        if(result.isSuccess()) {
            io.nuls.api.model.Transaction transaction = null;
            List<Utxo> list = utxoBusiness.getUsableUtxo(contractTransParam.getSender());
            if(contractTransParam.getTypes() == EntityConstant.TX_TYPE_CREATE_CONTRACT){
                transaction = TransactionTool.contractCreateTxForApi(
                    contractTransParam.getSender(),
                    contractTransParam.getGasLimit(),
                    contractTransParam.getPrice(),
                    Hex.decode(contractTransParam.getContractCode()),
                    null,
                    contractTransParam.getRemark(),
                    list
                );
            }else if(contractTransParam.getTypes() == EntityConstant.TX_TYPE_CALL_CONTRACT){
                transaction = TransactionTool.contractCallTxForApi(
                        contractTransParam.getSender(),
                        io.nuls.api.model.Na.valueOf(contractTransParam.getValue()),
                        contractTransParam.getGasLimit(),
                        contractTransParam.getPrice(),
                        contractTransParam.getContractAddress(),
                        contractTransParam.getMethodName(),
                        contractTransParam.getMethodDesc(),
                        null,
                        contractTransParam.getRemark(),
                        list

                    );
            }else if(contractTransParam.getTypes() == EntityConstant.TX_TYPE_DELETE_CONTRACT){
                transaction = TransactionTool.contractDeleteTxForApi(
                        contractTransParam.getSender(),
                        contractTransParam.getContractAddress(),
                        contractTransParam.getRemark(),
                        list);
            }else{
                //其他，暂时不处理
                return RpcClientResult.getFailed(KernelErrorCode.TX_TYPE_NULL);
            }
            if(null == transaction){
                return RpcClientResult.getFailed(KernelErrorCode.BALANCE_NOT_ENOUGH);
            }
            result.setData(transaction.getHash().getDigestHex());
            Transaction tx = RpcTransferUtil.toTransaction(transaction);
            Utxo utxoKey = null;
            for (Input input : tx.getInputs()) {
                utxoKey = utxoBusiness.getByKey(input.getKey());
                if (utxoKey == null) {
                    utxoKey = webwalletUtxoLevelDbService.select(contractTransParam.getSender());
                }
                input.setAddress(utxoKey.getAddress());
                input.setValue(utxoKey.getAmount());
            }
            WebwalletTransaction webwalletTransaction = new WebwalletTransaction(tx, Base64.getEncoder().encodeToString(transaction.serialize()), contractTransParam.getSender(), null);
            if (webwalletTransactionBusiness.save(webwalletTransaction) != 1) {
                return RpcClientResult.getFailed(KernelErrorCode.TX_SAVETEMPUTXO_ERROR);
            }
        }
        return result;
    }

    @POST
    @Path("/contractBroadcast")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult contractBroadcast(ContractTransParam contractTransParam) throws Exception {
        WebwalletTransaction transaction = webwalletTransactionBusiness.getByKey(contractTransParam.getHash());
        if(null != transaction){
            if(null != transactionBusiness.getByHash(transaction.getHash())){
                return RpcClientResult.getFailed(KernelErrorCode.TX_HASH_CONFIRMED_ERROR);
            }
            byte[] data = Base64.getDecoder().decode(transaction.getSignData());
            io.nuls.api.model.Transaction boradTx = null;
            if(contractTransParam.getTypes() == EntityConstant.TX_TYPE_CREATE_CONTRACT){
                boradTx = new CreateContractTransaction();
            }else if(contractTransParam.getTypes() == EntityConstant.TX_TYPE_CALL_CONTRACT){
                boradTx = new CallContractTransaction();
            }else if(contractTransParam.getTypes() == EntityConstant.TX_TYPE_DELETE_CONTRACT){
                boradTx = new DeleteContractTransaction();
            }else{
                //其他，暂时不处理
                return RpcClientResult.getFailed(KernelErrorCode.TX_TYPE_NULL);
            }
            boradTx.parse(new NulsByteBuffer(data));
            boradTx.setScriptSig(Hex.decode(contractTransParam.getSign()));
            return borad(transaction,Hex.encode(boradTx.serialize()));
        }else{
            return RpcClientResult.getFailed();
        }
    }

    @POST
    @Path("/broadcast")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcClientResult broadcast(TransactionParam transactionParam) throws Exception {
        if(null == transactionParam){
            return RpcClientResult.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        if(StringUtils.isBlank(transactionParam.getAgentHash())){
            return RpcClientResult.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        if(StringUtils.isBlank(transactionParam.getSign())){
            return RpcClientResult.getFailed(KernelErrorCode.NULL_PARAMETER);
        }
        WebwalletTransaction transaction = webwalletTransactionBusiness.getByKey(transactionParam.getAgentHash());
        if(null != transaction){
            if(null != transactionBusiness.getByHash(transaction.getHash())){
                return RpcClientResult.getFailed(KernelErrorCode.TX_HASH_CONFIRMED_ERROR);
            }
            byte[] data = Base64.getDecoder().decode(transaction.getSignData());
            io.nuls.api.model.Transaction boradTx = null;
            if(transaction.getType() == EntityConstant.TX_TYPE_TRANSFER){
                //转账
                boradTx = new TransferTransaction();
            }else if(transaction.getType() == EntityConstant.TX_TYPE_ACCOUNT_ALIAS){
                //设置别名
                boradTx = new AliasTransaction();
            }else if(transaction.getType() == EntityConstant.TX_TYPE_JOIN_CONSENSUS){
                //加入共识
                boradTx = new DepositTransaction();
            }else if(transaction.getType() == EntityConstant.TX_TYPE_CANCEL_DEPOSIT){
                //退出共识
                boradTx = new CancelDepositTransaction();
            }else{
                //其他，暂时不处理
                return RpcClientResult.getFailed(KernelErrorCode.PARAMETER_ERROR);
            }
            //94015

            boradTx.parse(new NulsByteBuffer(data));
            boradTx.setScriptSig(Hex.decode(transactionParam.getSign()));
            return borad(transaction,Hex.encode(boradTx.serialize()));
        }else{
            return RpcClientResult.getFailed();
        }
    }

    /**
     * 广播交易 将创建好的交易广播出去，同时删除leveldb里面缓存的webwalletTrans
     * @param transaction leveldb里面缓存的webwalletTrans
     * @param txHex 要广播的交易的hex
     * @return 广播的结果RpcClientResult
     */
    private RpcClientResult borad(WebwalletTransaction transaction,String txHex){
        Map<String, String> params = new HashMap<>(1);
        params.put("txHex", txHex);
        RpcClientResult result = syncDataHandler.broadcast(params);
        if(result.isSuccess()){
            //广播成功，签名数据update回去，同时保存utxo到leveldb
            transaction.setSignData(txHex);
            if(webwalletTransactionBusiness.update(transaction) > 0){
                return result;
            }else{
                return RpcClientResult.getFailed(KernelErrorCode.TX_SAVETEMPUTXO_ERROR);
            }
        }else{
            Log.error("交易"+transaction.getHash()+"验证失败，原因："+result.getData()+"，code:"+result.getCode());
            //删除交易和缓存
            webwalletTransactionBusiness.deleteByKey(transaction.getHash());
            webwalletUtxoLevelDbService.delete(transaction.getAddress());
            try{
                Map resultAttr = (Map)result.getData();
                return new RpcClientResult(false, resultAttr.get("code")+"", result.getMsg());
            }catch (Exception e){
                return RpcClientResult.getFailed(KernelErrorCode.TX_BROADCAST_ERROR);
            }
        }
    }

    /**
     * 没有同步到最新高度，禁止交易
     * @return 验证结果RpcClientResult
     */
    private RpcClientResult valiHeight(){
        BlockHeader blockHeader = blockBusiness.getNewest();
        try {
            RpcClientResult<BlockHeader> newestBlock = syncDataHandler.getNewest();
            if(!newestBlock.isSuccess() || (newestBlock.getData().getHeight()-blockHeader.getHeight())>1){
                return RpcClientResult.getFailed(KernelErrorCode.BLOCK_NOT_SYNC);
            }
        } catch (NulsException e) {
            return RpcClientResult.getFailed(KernelErrorCode.BLOCK_NOT_SYNC);
        }
        return RpcClientResult.getSuccess();
    }
}
