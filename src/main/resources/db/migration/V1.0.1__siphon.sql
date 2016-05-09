ALTER TABLE `pos_module`
ADD COLUMN `change_quantity` int(11) NOT NULL DEFAULT 0 AFTER `quantity`,
ADD COLUMN `diviation` int(11) NOT NULL DEFAULT 0 AFTER `change_quantity`;