CREATE TABLE `pos` (
  `itemid` bigint(20) NOT NULL,
  `allow_alliance_members` bit(1) NOT NULL,
  `allow_corp_members` bit(1) NOT NULL,
  `cached_until` datetime DEFAULT NULL,
  `fuel` int(11) NOT NULL,
  `moon` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `online_timestamp` datetime DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `state_timestamp` datetime DEFAULT NULL,
  `strontium` int(11) NOT NULL,
  `system` varchar(255) DEFAULT NULL,
  `system_id` int(11) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `typeid` bigint(20) NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pos_module` (
  `itemid` bigint(20) NOT NULL,
  `cached_until` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `content_typeid` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `change_quantity` int(11) NOT NULL DEFAULT '0',
  `diviation` int(11) NOT NULL DEFAULT '0',
  `system` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `typeid` bigint(20) NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `output_module_itemid` bigint(20) DEFAULT NULL,
  `pos_itemid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`itemid`),
  KEY `FK_pos_module_2_pos` (`pos_itemid`),
  KEY `FK_pos_module_2_pos_module` (`output_module_itemid`),
  CONSTRAINT `FK_pos_module_2_pos` FOREIGN KEY (`pos_itemid`) REFERENCES `pos` (`itemid`),
  CONSTRAINT `FK_pos_module_2_pos_module` FOREIGN KEY (`output_module_itemid`) REFERENCES `pos_module` (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

CREATE TABLE `configuration` (
  `id` INT NOT NULL,
  `eveapi_id` BIGINT NULL,
  `evaapi_vcode` VARCHAR(60) NULL,
  `xmpp_server` VARCHAR(120) NULL,
  `xmpp_port` INT NULL,
  `xmpp_username` VARCHAR(45) NULL,
  `xmpp_password` VARCHAR(45) NULL,
  `xmpp_broadcast` VARCHAR(120) NULL,
  `xmpp_insecure` BINARY NULL,
  `xmpp_statusmessage` VARCHAR(200) NULL,
  `pos_alerthours` INT NULL,
  `pos_broadcasthours` INT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `configuration`(`id`, `eveapi_id`, `xmpp_port`, `xmpp_insecure`, `pos_alerthours`, `pos_broadcasthours`) VALUES (1, 0, 5222, false, 48, 24);