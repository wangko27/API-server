
DROP TABLE IF EXISTS `demo`;

CREATE TABLE `demo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `byteCol` tinyint(3) DEFAULT NULL,
  `shortCol` smallint(5) DEFAULT NULL,
  `intCol` int(15) DEFAULT NULL,
  `floatCol` float(8,4) DEFAULT NULL,
  `doubleCol` double(20,5) DEFAULT NULL,
  `boolCol` bit(1) DEFAULT NULL,
  `blobCol` blob,
  `StringCol1` char(1) DEFAULT NULL,
  `StringCol2` varchar(32) DEFAULT NULL,
  `dateCol` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
