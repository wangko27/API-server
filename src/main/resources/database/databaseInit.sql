
CREATE DATABASE IF NOT EXISTS `nuls` DEFAULT CHARACTER SET utf8 ;

USE `nuls`;

CREATE TABLE IF NOT EXISTS `account` (
  `address` varchar(40) NOT NULL,
  `create_time` bigint(14) NOT NULL,
  `alias` varchar(100) DEFAULT NULL,
  `pub_key` varbinary(100) DEFAULT NULL,
  `pri_key` varbinary(100) DEFAULT NULL,
  `encrypted_pri_key` varbinary(100) DEFAULT NULL,
  `extend` varbinary(1024) DEFAULT NULL,
  `status` INT DEFAULT 0,
  PRIMARY KEY (`address`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `block_header` (
  `hash` varchar(70) NOT NULL,
  `height` bigint(14) NOT NULL,
  `pre_hash` varchar(70) ,
  `merkle_hash` varchar(70) NOT NULL,
  `create_time` bigint(14) NOT NULL,
  `consensus_address` varchar(40) DEFAULT NULL,
  `tx_count` int(5) NOT NULL,
  `round_index` bigint(14) NOT NULL,
  `scriptSig` varbinary(1024) ,
  `extend` varbinary(1024) NOT NULL,
  `size` int(9),
  PRIMARY KEY (`hash`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `delegate_account` (
  `id` varchar(32) NOT NULL,
  `address` varchar(40) NOT NULL,
  `node_address` varchar(40) NOT NULL,
  `deposit` bigint(18) NOT NULL,
  `remark` varchar(255) NOT NULL,
  `status` INT DEFAULT 0,
  `start_time` bigint(14) NOT NULL,
  `commission_rate` decimal(14) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `delegate` (
  `id` varchar(32) NOT NULL,
  `address` varchar(40) NOT NULL,
  `agent_address` varchar(40) NOT NULL,
  `deposit` bigint(18) NOT NULL,
  `status` int(1) DEFAULT NULL,
  `time` bigint(14) DEFAULT NULL,
  `block_height` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `node` (
  `id` VARCHAR(30) NOT NULL,
  `ip` varchar(20) NOT NULL,
  `port` int(6) NOT NULL,
  `last_time` bigint(20) NOT NULL,
  `last_fail_time` bigint(20) NOT NULL,
  `fail_count` int(1) NOT NULL,
  `status` int(1) NOT NULL,
  `magic_num` int(11) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `node_group` (
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `node_group_relation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `node_id` varchar(30) NOT NULL,
  `group_id` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `sub_chain` (
  `id` varchar(30) NOT NULL,
  `creator_address` varchar(40) NOT NULL,
  `tx_hash` varchar(70) NOT NULL,
  `g_block` varbinary(1024) NOT NULL,
  `g_block_hash` varchar(70) NOT NULL,
  `g_merkle_hash` varchar(70) NOT NULL,
  `g_block_header` varbinary(1024) NOT NULL,
  `title` varchar(255) NOT NULL,
  `sign` varbinary(1024) NOT NULL,
  `address_prefix` int(5) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `transaction` (
  `hash` varchar(70) NOT NULL,
  `tx_index` int(5) NOT NULL,
  `type` int(5) NOT NULL,
  `create_time` bigint(15) NOT NULL,
  `block_height` bigint(15) NOT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `fee` bigint(19) NOT NULL,
  `txData` varbinary(1024)  ,
  `scriptSig` varbinary(255) ,
  `size` int(9),
  PRIMARY KEY (`hash`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `transaction_local` (
  `hash` varchar(70) NOT NULL,
  `tx_index` int(5) NOT NULL,
  `type` int(5) NOT NULL,
  `create_time` bigint(15) NOT NULL,
  `block_height` bigint(15) NOT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `fee` bigint(19) NOT NULL,
  `transferType` int(1),
  `txData` varbinary(1024)  ,
  `scriptSig` varbinary(255) ,
  `size` int(9),
  PRIMARY KEY (`hash`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `tx_account_relation` (
  `tx_hash` varchar(70) NOT NULL,
  `address` varchar(40) NOT NULL,
  PRIMARY KEY (`tx_hash`, `address`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `utxo_input` (
  `tx_hash` varchar(70) NOT NULL,
  `in_index` int(5) NOT NULL,
  `from_hash` varchar(70) NOT NULL,
  `from_index` int(5) NOT NULL,
  PRIMARY KEY (`tx_hash`,`in_index`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `utxo_output` (
  `tx_hash` varchar(70) NOT NULL,
  `out_index` int(5) NOT NULL,
  `value` bigint(18) NOT NULL,
  `lock_time` bigint(20) DEFAULT NULL,
  `status` tinyint(1) NOT NULL,
  `script` varbinary(1024) NOT NULL,
  `address` varchar(40) NOT NULL,
  PRIMARY KEY (`tx_hash`,`out_index`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `alias` (
  `alias` VARCHAR(20) NOT NULL,
  `address` varchar(30) NOT NULL,
  `status` tinyint(1),
  PRIMARY KEY (`alias`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table IF NOT EXISTS balance_top (
    id bigint not null AUTO_INCREMENT,
    address varchar(40) comment '地址',
    balance bigint comment '余额',
    tx_count int comment '交易数量',
    create_time bigint comment '创建时间',
    primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table IF NOT EXISTS balance_top_temp (
    address varchar(40),
    balance bigint,
    tx_count int 
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table IF NOT EXISTS mined_top (
    id bigint not null AUTO_INCREMENT,
    consensus_address varchar(40) comment '出块地址',
    mined_count int comment '出块数量',
    reward bigint comment '总收益',
    last_height bigint comment '最新出块高度',
    create_time bigint comment '创建时间',
    primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table IF NOT EXISTS mined_top_temp (
    consensus_address varchar(40),
    mined_count int,
    reward bigint,
    last_height bigint 
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table IF NOT EXISTS tx_history (
    id bigint not null AUTO_INCREMENT,
    tx_date varchar(8) comment '交易日期',
    tx_count int comment '交易数量',
    create_time bigint comment '创建时间',
    primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
 -- alias index
 create unique index alias_address_idx on alias(address);