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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `pos_module` (
  `itemid` bigint(20) NOT NULL,
  `cached_until` datetime DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `content_typeid` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `system` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `typeid` bigint(20) NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `output_module_itemid` bigint(20) DEFAULT NULL,
  `pos_itemid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`itemid`),
  CONSTRAINT `FK_pos_module_2_pos` FOREIGN KEY (`pos_itemid`) REFERENCES `pos` (`itemid`),
  CONSTRAINT `FK_pos_module_2_pos_module` FOREIGN KEY (`output_module_itemid`) REFERENCES `pos_module` (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
