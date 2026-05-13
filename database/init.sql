-- 水产品加工监管系统 - 数据库初始化脚本
-- Database: aquatic_supervision

CREATE DATABASE IF NOT EXISTS aquatic_supervision DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aquatic_supervision;

-- 1. 用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `role` VARCHAR(30) NOT NULL COMMENT '角色: ADMIN/PROD_MANAGER/INSPECTOR/OPERATOR',
  `employee_no` VARCHAR(20) COMMENT '员工编号',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 企业信息表
DROP TABLE IF EXISTS `enterprise_info`;
CREATE TABLE `enterprise_info` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(200) NOT NULL COMMENT '企业名称',
  `license_no` VARCHAR(100) COMMENT '生产许可证编号',
  `address` VARCHAR(300) COMMENT '生产地址',
  `phone` VARCHAR(30) COMMENT '联系电话',
  `scope` TEXT COMMENT '经营范围',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';

-- 3. 批次表
DROP TABLE IF EXISTS `batches`;
CREATE TABLE `batches` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `batch_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '批次编号',
  `product_type` VARCHAR(100) NOT NULL COMMENT '产品类型',
  `raw_material_type` VARCHAR(100) COMMENT '原料类型',
  `source` VARCHAR(200) COMMENT '来源/供应商',
  `origin` VARCHAR(200) COMMENT '产地',
  `receipt_time` DATETIME COMMENT '收货时间',
  `quantity` DECIMAL(12,2) COMMENT '数量(kg)',
  `certificate_no` VARCHAR(100) COMMENT '证书编号',
  `remark` TEXT COMMENT '备注',
  `operator_id` BIGINT COMMENT '登记人员ID',
  `production_status` VARCHAR(30) DEFAULT 'created' COMMENT '生产状态: created/processing/completed',
  `release_status` VARCHAR(30) DEFAULT 'unreleased' COMMENT '放行状态: unreleased/released',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次表';

-- 4. 来料质检表
DROP TABLE IF EXISTS `incoming_inspections`;
CREATE TABLE `incoming_inspections` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `batch_id` BIGINT NOT NULL COMMENT '批次ID',
  `inspector_id` BIGINT COMMENT '检验员ID',
  `result` VARCHAR(20) DEFAULT 'PENDING' COMMENT '结果: PENDING/PASS/FAIL',
  `disposal` VARCHAR(100) COMMENT '处置方式',
  `remark` TEXT COMMENT '检验备注',
  `inspect_time` DATETIME COMMENT '检验时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='来料质检表';

-- 5. 成品质检表
DROP TABLE IF EXISTS `finished_inspections`;
CREATE TABLE `finished_inspections` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `batch_id` BIGINT NOT NULL COMMENT '批次ID',
  `inspector_id` BIGINT COMMENT '检验员ID',
  `result` VARCHAR(20) DEFAULT 'PENDING' COMMENT '结果: PENDING/PASS/FAIL',
  `items` VARCHAR(300) COMMENT '检验项目',
  `remark` TEXT COMMENT '备注',
  `inspect_time` DATETIME COMMENT '检验时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成品质检表';

-- 6. 生产工序记录表
DROP TABLE IF EXISTS `production_records`;
CREATE TABLE `production_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `batch_id` BIGINT NOT NULL COMMENT '批次ID',
  `process_type` VARCHAR(50) NOT NULL COMMENT '工序类型: sorting/cleaning/packaging',
  `operator_id` BIGINT COMMENT '操作员ID',
  `equipment` VARCHAR(200) COMMENT '设备名称',
  `record_time` DATETIME COMMENT '记录时间',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产工序记录表';

-- 7. 放行记录表
DROP TABLE IF EXISTS `release_records`;
CREATE TABLE `release_records` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `batch_id` BIGINT NOT NULL COMMENT '批次ID',
  `manager_id` BIGINT COMMENT '放行人ID',
  `release_time` DATETIME COMMENT '放行时间',
  `remark` TEXT COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='放行记录表';

-- 8. 追溯码表
DROP TABLE IF EXISTS `traceability_codes`;
CREATE TABLE `traceability_codes` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `batch_id` BIGINT NOT NULL COMMENT '批次ID',
  `trace_code` VARCHAR(50) NOT NULL UNIQUE COMMENT '追溯码',
  `qr_url` VARCHAR(300) COMMENT '二维码链接',
  `is_public` TINYINT DEFAULT 1 COMMENT '是否公开: 1是 0否',
  `generate_time` DATETIME COMMENT '生成时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='追溯码表';

-- 9. 系统公告表
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content` TEXT COMMENT '公告内容',
  `publisher_id` BIGINT COMMENT '发布人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

-- ============ 初始数据 ============

-- 用户数据 (密码通过 DataInitializer 自动加密为BCrypt，初始SQL密码为明文)
INSERT INTO `users` (`username`, `password`, `real_name`, `role`, `employee_no`, `status`) VALUES
('admin1', '123456', 'Admin User', 'ADMIN', 'AD-001', 1),
('manager1', '123456', 'Manager Zhang', 'PROD_MANAGER', 'MG-001', 1),
('inspector1', '123456', 'Inspector Li', 'INSPECTOR', 'QI-001', 1),
('operator1', '123456', 'Operator Wang', 'OPERATOR', 'OP-001', 1);

-- 企业信息
INSERT INTO `enterprise_info` (`name`, `license_no`, `address`, `phone`, `scope`) VALUES
('福建东山岛水产加工有限公司', 'SC-2025-FJ-0892', '福建省漳州市东山县铜陵镇海产工业园A区', '0596-5888888', '水产加工品（速冻水产品、干制水产品）、速冻食品生产与销售；水产品养殖、收购、销售；货物或技术进出口。');

-- 批次数据
INSERT INTO `batches` (`batch_no`, `product_type`, `raw_material_type`, `source`, `origin`, `receipt_time`, `quantity`, `certificate_no`, `remark`, `operator_id`, `production_status`, `release_status`) VALUES
('BT-20250512-003', '冻虾仁', '南美白对虾', '福建东山岛海产养殖基地', '福建省漳州市东山县', '2025-05-12 06:15:00', 1250.00, 'SC-2025-FJ-0892', '冷链运输，到货温度-18°C，外包装完好', 4, 'completed', 'unreleased'),
('BT-20250511-002', '速冻鱼片', '罗非鱼', '广东湛江水产养殖基地', '广东省湛江市', '2025-05-11 05:30:00', 800.00, 'SC-2025-GD-0456', '冷链运输', 4, 'processing', 'unreleased'),
('BT-20250510-001', '墨鱼丸', '墨鱼', '浙江舟山远洋渔业', '浙江省舟山市', '2025-05-10 04:00:00', 500.00, 'SC-2025-ZJ-0234', '冷冻运输', 4, 'created', 'unreleased'),
('BT-20250509-004', '鱿鱼圈', '鱿鱼', '福建东山岛海产养殖基地', '福建省漳州市东山县', '2025-05-09 07:00:00', 600.00, 'SC-2025-FJ-0912', '冷链运输', 4, 'created', 'unreleased'),
('BT-20250508-005', '冻虾仁', '南美白对虾', '福建东山岛海产养殖基地', '福建省漳州市东山县', '2025-05-08 06:45:00', 1100.00, 'SC-2025-FJ-0901', '冷链运输', 4, 'completed', 'unreleased'),
('BT-20250506-008', '冻虾仁', '南美白对虾', '福建东山岛海产养殖基地', '福建省漳州市东山县', '2025-05-06 05:30:00', 1500.00, 'SC-2025-FJ-0850', '冷链运输，到货温度-18°C', 4, 'completed', 'released'),
('BT-20250505-009', '速冻鱼片', '罗非鱼', '广东湛江水产养殖基地', '广东省湛江市', '2025-05-05 04:00:00', 950.00, 'SC-2025-GD-0401', '冷链运输', 4, 'completed', 'released'),
('BT-20250504-010', '冻虾仁', '南美白对虾', '福建东山岛海产养殖基地', '福建省漳州市东山县', '2025-05-04 07:30:00', 1350.00, 'SC-2025-FJ-0810', '冷链运输', 4, 'completed', 'released');

-- 来料质检数据
INSERT INTO `incoming_inspections` (`batch_id`, `inspector_id`, `result`, `disposal`, `remark`, `inspect_time`) VALUES
(1, 3, 'PASS', '准许加工', '感官正常，药残检测合格，中心温度-18.2°C', '2025-05-12 09:15:00'),
(2, 3, 'PASS', '准许加工', '合格', '2025-05-11 08:30:00'),
(3, 3, 'PENDING', NULL, NULL, NULL),
(4, 3, 'PENDING', NULL, NULL, NULL),
(5, 3, 'PASS', '准许加工', '合格', '2025-05-08 10:00:00'),
(6, 3, 'PASS', '准许加工', '感官正常，药残检测合格', '2025-05-06 08:00:00'),
(7, 3, 'PASS', '准许加工', '合格', '2025-05-05 07:30:00'),
(8, 3, 'PASS', '准许加工', '合格', '2025-05-04 09:00:00');

-- 成品质检数据
INSERT INTO `finished_inspections` (`batch_id`, `inspector_id`, `result`, `items`, `remark`, `inspect_time`) VALUES
(1, 3, 'PASS', '净含量、感官、微生物、中心温度、标签标识', '全部项目合格，准予放行', '2025-05-12 16:00:00'),
(5, 3, 'PASS', '净含量、感官、微生物、中心温度', '全部项目合格，准予放行', '2025-05-08 15:00:00'),
(6, 3, 'PASS', '净含量、感官、微生物、中心温度、标签标识', '全部项目合格，准予放行', '2025-05-06 15:30:00'),
(7, 3, 'PASS', '净含量、感官、微生物、中心温度', '全部项目合格，准予放行', '2025-05-05 14:00:00'),
(8, 3, 'PASS', '净含量、感官、微生物、中心温度、标签标识', '全部项目合格，准予放行', '2025-05-04 16:00:00');

-- 生产工序记录
INSERT INTO `production_records` (`batch_id`, `process_type`, `operator_id`, `equipment`, `record_time`) VALUES
(1, 'sorting', 4, '自动分拣线 A1', '2025-05-12 10:00:00'),
(1, 'sorting', 4, '重量分选机 B2', '2025-05-12 10:45:00'),
(1, 'cleaning', 4, '气泡清洗机 C1', '2025-05-12 11:20:00'),
(1, 'packaging', 4, '真空包装机 D1', '2025-05-12 13:00:00'),
(6, 'sorting', 4, '自动分拣线 A1', '2025-05-06 09:00:00'),
(6, 'cleaning', 4, '气泡清洗机 C1', '2025-05-06 10:30:00'),
(6, 'packaging', 4, '真空包装机 D1', '2025-05-06 13:00:00'),
(7, 'sorting', 4, '自动分拣线 A2', '2025-05-05 08:00:00'),
(7, 'cleaning', 4, '气泡清洗机 C2', '2025-05-05 09:30:00'),
(7, 'packaging', 4, '真空包装机 D2', '2025-05-05 11:00:00'),
(8, 'sorting', 4, '自动分拣线 A1', '2025-05-04 08:30:00'),
(8, 'cleaning', 4, '气泡清洗机 C1', '2025-05-04 10:00:00'),
(8, 'packaging', 4, '真空包装机 D1', '2025-05-04 12:30:00');

-- 放行记录
INSERT INTO `release_records` (`batch_id`, `manager_id`, `release_time`) VALUES
(6, 2, '2025-05-06 17:00:00'),
(7, 2, '2025-05-05 16:30:00'),
(8, 2, '2025-05-04 17:30:00');

-- 追溯码
INSERT INTO `traceability_codes` (`batch_id`, `trace_code`, `qr_url`, `is_public`, `generate_time`) VALUES
(6, 'AQ-2025-008-M2P4', 'http://localhost:8080/api/public/trace/AQ-2025-008-M2P4', 1, '2025-05-06 17:00:00'),
(7, 'AQ-2025-009-K8N1', 'http://localhost:8080/api/public/trace/AQ-2025-009-K8N1', 1, '2025-05-05 16:30:00'),
(8, 'AQ-2025-010-W3R5', 'http://localhost:8080/api/public/trace/AQ-2025-010-W3R5', 1, '2025-05-04 17:30:00');

-- 系统公告
INSERT INTO `announcements` (`title`, `content`, `publisher_id`) VALUES
('关于五一劳动节放假期间生产安排的通知', '根据公司安排，五一劳动节期间（5月1日至5月5日）生产线将进行设备维护与消毒工作。请各部门提前做好原料库存盘点，确保节后生产顺利进行。质检部门需完成节前全部批次质检工作。', 1);
