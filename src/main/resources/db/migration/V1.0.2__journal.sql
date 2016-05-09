CREATE TABLE `wallet_journal` (
  `refid` bigint(20) NOT NULL,
  `ref_typeid` int(11) NOT NULL,
  `date` datetime DEFAULT NULL,
  `owner_name1` varchar(255) DEFAULT NULL,
  `ownerid1` bigint(20) NOT NULL,
  `owner_name2` varchar(255) DEFAULT NULL,
  `ownerid2` bigint(20) NOT NULL,
  `arg_name` varchar(255) DEFAULT NULL,
  `argid` bigint(20) NOT NULL,
  `amount` double NOT NULL,
  `balance` double NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `tax_receiverid` bigint(20) DEFAULT NULL,
  `tax_amount` double DEFAULT NULL,
  `tax_rate` double NOT NULL,
  PRIMARY KEY (`refid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
