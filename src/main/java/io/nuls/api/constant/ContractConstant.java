/**
 * 智能合约相关静态配置
 */
package io.nuls.api.constant;

public interface ContractConstant extends NulsConstant {

    short MODULE_ID_CONTRACT = 10;

    /**
     * CONTRACT
     */
    int TX_TYPE_CREATE_CONTRACT = 100;
    int TX_TYPE_CALL_CONTRACT = 101;
    int TX_TYPE_DELETE_CONTRACT = 102;

    /**
     * contract transfer
     */
    int TX_TYPE_CONTRACT_TRANSFER = 103;
    long CONTRACT_TRANSFER_GAS_COST = 1000;

    String BALANCE_TRIGGER_METHOD_NAME = "_payable";
    String BALANCE_TRIGGER_METHOD_DESC = "() return void";

    String CONTRACT_CONSTRUCTOR = "<init>";




    String CALL = "call";
    String CREATE = "create";
    String DELETE = "delete";

    String GET = "get";

    String SEND_BACK_REMARK = "Contract execution failed, return funds.";

    long CONTRACT_CONSTANT_GASLIMIT = 1000000;
    long CONTRACT_CONSTANT_PRICE = 1;

    long MAX_GASLIMIT = 10000000;

    /**
     *
     */
    String CONTRACT_EVENT = "event";
    String CONTRACT_EVENT_DATA = "payload";

    /**
     * NRC20
     */
    String NRC20_METHOD_NAME = "name";
    String NRC20_METHOD_SYMBOL = "symbol";
    String NRC20_METHOD_DECIMALS = "decimals";
    String NRC20_METHOD_TOTAL_SUPPLY = "totalSupply";
    String NRC20_METHOD_BALANCE_OF = "balanceOf";
    String NRC20_METHOD_TRANSFER = "transfer";
    String NRC20_METHOD_TRANSFER_FROM = "transferFrom";
    String NRC20_METHOD_APPROVE = "approve";
    String NRC20_METHOD_ALLOWANCE = "allowance";
    String NRC20_EVENT_TRANSFER = "TransferEvent";
    String NRC20_EVENT_APPROVAL = "ApprovalEvent";

    int CONTRACT_STATUS_FAILED = 0;
    int CONTRACT_STATUS_NORMAL = 1;
    int CONTRACT_STATUS_DELETED = 2;

}
