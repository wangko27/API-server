/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.21 : Database - nuls_apiserver
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`nuls_apiserver` /*!40100 DEFAULT CHARACTER SET utf8 */;

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

/*Table structure for table `block_header` */

DROP TABLE IF EXISTS `block_header`;

CREATE TABLE `block_header` (
  `height` bigint(15) NOT NULL COMMENT '高度',
  `hash` varchar(80) NOT NULL COMMENT 'hash',
  `consensus_address` varchar(40) DEFAULT NULL COMMENT '共识地址',
  `create_time` bigint(18) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`height`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区块头';

/*Data for the table `block_header` */

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

/*Table structure for table `transaction` */

DROP TABLE IF EXISTS `transaction`;

CREATE TABLE `transaction` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `hash` varchar(80) NOT NULL COMMENT '交易hash',
  `block_height` bigint(15) NOT NULL COMMENT '区块高度',
  PRIMARY KEY (`id`),
  KEY `block_height_idx` (`block_height`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易记录表';

/*Data for the table `transaction` */

/*Table structure for table `transaction_relation` */

DROP TABLE IF EXISTS `transaction_relation`;

CREATE TABLE `transaction_relation` (
  `id` bigint(15) NOT NULL AUTO_INCREMENT,
  `address` varchar(40) NOT NULL COMMENT 'address',
  `tx_hash` varchar(80) NOT NULL COMMENT 'tx_hash',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `transaction_relation` */

/*Table structure for table `utxo` */

DROP TABLE IF EXISTS `utxo`;

CREATE TABLE `utxo` (
  `hash_index` varchar(76) NOT NULL COMMENT 'tx_hash和tx_index',
  `address` varchar(40) NOT NULL,
  PRIMARY KEY (`hash_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='UTXO';

/*Data for the table `utxo` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
