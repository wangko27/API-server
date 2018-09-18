/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.5.49-log : Database - nuls_apiserver
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`nuls_apiserver` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `nuls_apiserver`;

/*Table structure for table `address_reward_detail` */

DROP TABLE IF EXISTS `address_reward_detail`;

CREATE TABLE `address_reward_detail` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `block_height` bigint(15) NOT NULL COMMENT '高度',
  `address` varchar(40) DEFAULT NULL COMMENT '地址',
  `amount` bigint(19) DEFAULT '0' COMMENT '奖励金额',
  `tx_hash` varchar(80) DEFAULT NULL COMMENT '交易hash',
  `create_time` bigint(15) DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`),
  KEY `height_idx` (`block_height`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户奖励明细表';

/*Data for the table `address_reward_detail` */

LOCK TABLES `address_reward_detail` WRITE;

UNLOCK TABLES;

/*Table structure for table `agent_node` */

DROP TABLE IF EXISTS `agent_node`;

CREATE TABLE `agent_node` (
  `tx_hash` varchar(80) NOT NULL COMMENT '创建节点 交易hash',
  `agent_address` varchar(40) DEFAULT NULL COMMENT '创建地址',
  `packing_address` varchar(40) DEFAULT NULL COMMENT '出块地址',
  `reward_address` varchar(40) DEFAULT NULL COMMENT '奖励地址',
  `deposit` bigint(19) DEFAULT NULL COMMENT '保证金金额',
  `commission_rate` decimal(5,2) DEFAULT '0.00' COMMENT '佣金比例',
  `create_time` bigint(15) DEFAULT NULL COMMENT '创建时间',
  `block_height` bigint(15) DEFAULT NULL COMMENT '创建高度',
  `status` int(1) DEFAULT NULL COMMENT '节点状态(0待共识，1共识中)',
  `total_deposit` bigint(19) DEFAULT NULL COMMENT '委托总金额',
  `deposit_count` int(5) DEFAULT NULL COMMENT '委托数量',
  `credit_value` decimal(9,8) DEFAULT NULL COMMENT '信用值',
  `total_packing_count` bigint(15) DEFAULT '0' COMMENT '累计出块数量',
  `last_reward_height` bigint(15) DEFAULT NULL COMMENT '最后收益区块高度',
  `total_reward` bigint(15) DEFAULT NULL COMMENT '共识总奖励金额',
  `delete_hash` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`tx_hash`),
  KEY `packing_address_idx` (`packing_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='所有的节点列表';

/*Data for the table `agent_node` */

LOCK TABLES `agent_node` WRITE;

UNLOCK TABLES;

/*Table structure for table `alias` */

DROP TABLE IF EXISTS `alias`;

CREATE TABLE `alias` (
  `alias` varchar(40) NOT NULL COMMENT '别名',
  `address` varchar(40) NOT NULL COMMENT '地址',
  `block_height` bigint(15) DEFAULT NULL COMMENT '高度',
  PRIMARY KEY (`alias`),
  UNIQUE KEY `alias_address_idx` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='别名表';

/*Data for the table `alias` */

LOCK TABLES `alias` WRITE;

UNLOCK TABLES;

/*Table structure for table `balance` */

DROP TABLE IF EXISTS `balance`;

CREATE TABLE `balance` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `address` varchar(40) NOT NULL COMMENT '地址',
  `locked` bigint(19) NOT NULL COMMENT '冻结金额',
  `usable` bigint(19) NOT NULL COMMENT '可用金额',
  `block_height` bigint(15) DEFAULT NULL COMMENT '块高度',
  `assets_code` varchar(10) DEFAULT NULL COMMENT '资产(nuls、blo)',
  PRIMARY KEY (`id`),
  KEY `address_idx` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='账户资产表';

/*Data for the table `balance` */

LOCK TABLES `balance` WRITE;

UNLOCK TABLES;

/*Table structure for table `block_header` */

DROP TABLE IF EXISTS `block_header`;

CREATE TABLE `block_header` (
  `height` bigint(15) NOT NULL COMMENT '高度',
  `hash` varchar(80) NOT NULL COMMENT 'hash',
  `consensus_address` varchar(40) DEFAULT NULL COMMENT '共识地址',
  `create_time` bigint(15) DEFAULT NULL COMMENT '出块时间',
  PRIMARY KEY (`height`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区块头';

/*Data for the table `block_header` */

LOCK TABLES `block_header` WRITE;

UNLOCK TABLES;

/*Table structure for table `deposit` */

DROP TABLE IF EXISTS `deposit`;

CREATE TABLE `deposit` (
  `tx_hash` varchar(80) NOT NULL COMMENT '委托 交易hash',
  `amount` bigint(19) DEFAULT NULL COMMENT '委托金额',
  `agent_hash` varchar(80) DEFAULT NULL COMMENT '节点hash',
  `agent_name` varchar(50) DEFAULT NULL COMMENT '节点名称',
  `address` varchar(40) DEFAULT NULL COMMENT '账户地址',
  `create_time` bigint(15) DEFAULT NULL COMMENT '委托时间',
  `block_height` bigint(15) DEFAULT NULL COMMENT '区块高度',
  `delete_hash` varchar(80) DEFAULT NULL COMMENT '删除节点时区块高度',
  PRIMARY KEY (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='委托记录表';

/*Data for the table `deposit` */

LOCK TABLES `deposit` WRITE;

UNLOCK TABLES;

/*Table structure for table `punish_log` */

DROP TABLE IF EXISTS `punish_log`;

CREATE TABLE `punish_log` (
  `id` bigint(35) NOT NULL AUTO_INCREMENT COMMENT '标识',
  `type` int(1) DEFAULT NULL COMMENT '0：黄牌，1：红牌',
  `address` varchar(40) DEFAULT NULL COMMENT '地址',
  `time` bigint(15) DEFAULT NULL COMMENT '时间',
  `block_height` bigint(15) DEFAULT NULL COMMENT '高度',
  `round_index` bigint(15) DEFAULT NULL COMMENT '轮次',
  `reason` varchar(100) DEFAULT NULL COMMENT '原因',
  `evidence` blob COMMENT '证据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='所有账户的惩罚记录、包括红牌和黄牌';

/*Data for the table `punish_log` */

LOCK TABLES `punish_log` WRITE;

UNLOCK TABLES;

/*Table structure for table `transaction` */

DROP TABLE IF EXISTS `transaction`;

CREATE TABLE `transaction` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `hash` varchar(80) NOT NULL COMMENT '交易hash',
  `block_height` bigint(15) NOT NULL COMMENT '区块高度',
  `type` int(2) DEFAULT NULL COMMENT '交易类型',
  `create_time` bigint(15) DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`),
  KEY `relation_create_time_idx` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易记录表';

/*Data for the table `transaction` */

LOCK TABLES `transaction` WRITE;

UNLOCK TABLES;

/*Table structure for table `transaction_relation` */

DROP TABLE IF EXISTS `transaction_relation`;

CREATE TABLE `transaction_relation` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `address` varchar(40) NOT NULL COMMENT 'address',
  `tx_hash` varchar(80) NOT NULL COMMENT 'tx_hash',
  `type` int(2) NOT NULL,
  `create_time` bigint(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `relation_address_idx` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `transaction_relation` */

LOCK TABLES `transaction_relation` WRITE;

UNLOCK TABLES;

/*Table structure for table `utxo` */

DROP TABLE IF EXISTS `utxo`;

CREATE TABLE `utxo` (
  `hash_index` varchar(76) NOT NULL COMMENT 'tx_hash和tx_index',
  `address` varchar(40) NOT NULL,
  PRIMARY KEY (`hash_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='UTXO';

/*Data for the table `utxo` */

LOCK TABLES `utxo` WRITE;

UNLOCK TABLES;

/*Table structure for table `webwallet_transaction` */

DROP TABLE IF EXISTS `webwallet_transaction`;

CREATE TABLE `webwallet_transaction` (
  `hash` varchar(80) NOT NULL,
  `type` int(2) DEFAULT NULL COMMENT '交易类型',
  `status` int(2) DEFAULT NULL COMMENT '状态1待确认，2已确认，3未广播',
  `time` bigint(15) DEFAULT NULL COMMENT '时间',
  `address` varchar(40) DEFAULT NULL COMMENT '发起者',
  `temp` varchar(80) DEFAULT NULL COMMENT '自定义值，可以是别名，备注，地址等',
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `webwallet_transaction` */

LOCK TABLES `webwallet_transaction` WRITE;

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

/*
智能合约相关表
Date: 2018-09-18 09:56:19
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for contract_address_info
-- ----------------------------
DROP TABLE IF EXISTS `contract_address_info`;
CREATE TABLE `contract_address_info` (
  `contract_address` varchar(40) NOT NULL COMMENT '智能合约地址',
  `creater` varchar(40) DEFAULT NULL COMMENT '创建地址',
  `create_tx_hash` varchar(80) NOT NULL COMMENT '创建合约交易hash',
  `block_height` bigint(19) DEFAULT NULL COMMENT '创建高度',
  `is_nrc20` int(1) DEFAULT NULL COMMENT '是否支持NRC20协议(0-否、1-是)',
  `status` int(1) DEFAULT NULL COMMENT '状态：(0-失败、1-正常、2-停止)',
  `create_time` bigint(15) DEFAULT NULL COMMENT '创建时间',
  `delete_hash` varchar(80) DEFAULT NULL COMMENT '删除合约交易hash',
  PRIMARY KEY (`contract_address`),
  KEY `contrack_address_txhash_idx` (`create_tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='智能合约地址表';

-- ----------------------------
-- Table structure for contract_call_info
-- ----------------------------
DROP TABLE IF EXISTS `contract_call_info`;
CREATE TABLE `contract_call_info` (
  `contract_address` varchar(40) DEFAULT NULL COMMENT '合约地址',
  `create_tx_hash` varchar(80) DEFAULT NULL COMMENT '交易哈希',
  `creater` varchar(40) DEFAULT NULL COMMENT '创建者地址',
  `gas_limit` bigint(19) DEFAULT NULL COMMENT '最大gas消耗',
  `price` bigint(19) DEFAULT NULL COMMENT '执行合约单价',
  `method_name` varchar(80) DEFAULT NULL COMMENT '方法名称',
  `method_desc` varchar(200) DEFAULT NULL COMMENT '方法签名',
  `args` varchar(200) DEFAULT NULL COMMENT '调用合约参数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='智能合约调用交易表';

-- ----------------------------
-- Table structure for contract_create_info
-- ----------------------------
DROP TABLE IF EXISTS `contract_create_info`;
CREATE TABLE `contract_create_info` (
  `create_tx_hash` varchar(80) NOT NULL COMMENT '创建合约交易hash',
  `contract_address` varchar(40) NOT NULL COMMENT '智能合约地址',
  `creater` varchar(40) DEFAULT NULL COMMENT '创建地址',
  `contract_code` text COMMENT '智能合约代码（HEX编码）',
  `gasLimit` bigint(19) DEFAULT NULL COMMENT '最大gas消耗',
  `price` bigint(19) DEFAULT NULL COMMENT '执行合约单价',
  `args` text COMMENT '创建合约参数',
  `methods` text COMMENT '合约包括方法',
  `create_time` bigint(15) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`create_tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='智能合约新增交易表';

-- ----------------------------
-- Table structure for contract_delete_info
-- ----------------------------
DROP TABLE IF EXISTS `contract_delete_info`;
CREATE TABLE `contract_delete_info` (
  `contract_address` varchar(40) NOT NULL COMMENT '智能合约地址',
  `creater` varchar(40) DEFAULT NULL COMMENT '创建地址',
  `create_tx_hash` varchar(80) NOT NULL COMMENT '创建合约交易hash',
  PRIMARY KEY (`create_tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='智能合约删除交易表';

-- ----------------------------
-- Table structure for contract_result_info
-- ----------------------------
DROP TABLE IF EXISTS `contract_result_info`;
CREATE TABLE `contract_result_info` (
  `tx_hash` varchar(80) NOT NULL COMMENT '合约交易hash',
  `contract_address` varchar(40) NOT NULL COMMENT '合约地址',
  `success` varchar(10) DEFAULT NULL COMMENT '交易是否成功',
  `error_message` varchar(100) DEFAULT NULL COMMENT '执行失败信息',
  `result` text COMMENT '执行结果',
  `gas_limit` bigint(19) DEFAULT NULL COMMENT 'gas最大限制',
  `gas_used` bigint(19) DEFAULT NULL COMMENT '已使用Gas',
  `price` bigint(19) DEFAULT NULL COMMENT '单价',
  `total_fee` bigint(19) DEFAULT NULL COMMENT '交易总手续费',
  `tx_size_fee` bigint(19) DEFAULT NULL COMMENT '交易大小手续费',
  `actual_contract_fee` bigint(19) DEFAULT NULL COMMENT '实际执行合约手续费',
  `refund_fee` bigint(19) DEFAULT NULL COMMENT '退还的手续费',
  `stateroot` varchar(100) DEFAULT NULL COMMENT '状态根',
  `tx_value` bigint(19) DEFAULT NULL COMMENT '交易附带的货币量',
  `stacktrace` text DEFAULT NULL COMMENT '堆栈踪迹',
  `balance` bigint(19) DEFAULT NULL COMMENT '合约余额',
  `nonce` bigint(19) DEFAULT NULL COMMENT '随机数',
  `transfers` text COMMENT '合约内部转账过程JSON数据',
  `events` text COMMENT '合约事件过程JSON数据',
  `token_transfers` text COMMENT '合约代币转账过程JSON数据',
  `token_name` varchar(50) DEFAULT NULL COMMENT '代币名称',
  `symbol` varchar(20) DEFAULT NULL COMMENT '代币符号',
  `decimals` bigint(19) DEFAULT NULL COMMENT '货币小数位精度',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `create_time` bigint(15) DEFAULT NULL COMMENT '交易时间',
  PRIMARY KEY (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='智能合约交易返回结果表';

-- ----------------------------
-- Table structure for contract_token_assets
-- ----------------------------
DROP TABLE IF EXISTS `contract_token_assets`;
CREATE TABLE `contract_token_assets` (
  `contract_address` varchar(80) NOT NULL COMMENT '智能合约地址',
  `account_address` varchar(80) NOT NULL COMMENT '账户地址',
  `amount` varchar(100) NOT NULL COMMENT '金额',
  `hash` varchar(80) NOT NULL,
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='NRC20代币资产表';

-- ----------------------------
-- Table structure for contract_token_info
-- ----------------------------
DROP TABLE IF EXISTS `contract_token_info`;
CREATE TABLE `contract_token_info` (
  `contract_address` varchar(40) NOT NULL COMMENT '智能合约地址',
  `tx_hash` varchar(80) NOT NULL COMMENT '创建合约交易hash',
  `token_name` varchar(50) DEFAULT NULL COMMENT '代币名',
  `symbol` varchar(50) DEFAULT NULL COMMENT '代币符号',
  `decimals` bigint(19) DEFAULT NULL COMMENT '精度',
  `totalsupply` varchar(100) DEFAULT NULL COMMENT '总供应量',
  `create_time` bigint(15) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='NRC20代币信息表';

-- ----------------------------
-- Table structure for contract_token_transfer_info
-- ----------------------------
DROP TABLE IF EXISTS `contract_token_transfer_info`;
CREATE TABLE `contract_token_transfer_info` (
  `tx_hash` varchar(80) NOT NULL COMMENT '创建合约交易hash',
  `from_address` varchar(40) DEFAULT NULL COMMENT '转出地址',
  `to_address` varchar(40) DEFAULT NULL COMMENT '转入地址',
  `tx_value` varchar(200) DEFAULT NULL COMMENT '转账金额',
  `create_time` bigint(15) DEFAULT NULL COMMENT '交易时间',
  `contract_address` varchar(40) DEFAULT NULL,
  `create_tx_hash` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='NRC20代币转账信息表';

-- ----------------------------
-- Table structure for contract_transaction
-- ----------------------------
DROP TABLE IF EXISTS `contract_transaction`;
CREATE TABLE `contract_transaction` (
  `contract_address` varchar(40) NOT NULL COMMENT '智能合约地址',
  `tx_hash` varchar(80) NOT NULL COMMENT '创建合约交易hash',
  `tx_type` int(4) DEFAULT NULL COMMENT '交易类型：（100-创建、101-调用合约、102-删除）',
  `creater` varchar(40) DEFAULT NULL COMMENT '交易创建来源地址',
  `status` int(1) DEFAULT NULL COMMENT '交易状态（1-已确认、0-未确认）',
  `create_time` bigint(15) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='智能合约交易记录表';

/*Table structure for table `contract_transfer_info` */
DROP TABLE IF EXISTS `contract_transfer_info`;
CREATE TABLE `contract_transfer_info` (
  `tx_hash` varchar(80) NOT NULL COMMENT '合约内部转账交易hash',
  `orgin_tx_hash` varchar(80) NOT NULL COMMENT '调用合约交易原hash',
  `contract_address` varchar(40) NOT NULL COMMENT '智能合约地址',
  `from_address` varchar(40) DEFAULT NULL COMMENT '来源地址',
  `to_address` varchar(40) DEFAULT NULL COMMENT '转入地址',
  `tx_value` bigint(19) DEFAULT NULL COMMENT '转账金额',
  `create_time` bigint(15) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='智能合约内部转账交易表';
