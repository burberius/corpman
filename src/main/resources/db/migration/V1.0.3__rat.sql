CREATE TABLE `rat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL,
  `journal_id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `groupid` int(11) DEFAULT NULL,
  `group_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `journal_rats` (`journal_id`),
  CONSTRAINT `FK_journal_2_rats` FOREIGN KEY (`journal_id`) REFERENCES `wallet_journal` (`refid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;