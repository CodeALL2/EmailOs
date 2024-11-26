-- MySQL dump 10.13  Distrib 8.0.40, for Linux (x86_64)
--
-- Host: localhost    Database: email
-- ------------------------------------------------------
-- Server version	8.0.40-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attachment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '附件唯一标识',
  `email_id` bigint unsigned NOT NULL COMMENT '所属邮件的 ID',
  `file_name` varchar(255) NOT NULL COMMENT '附件文件名',
  `file_size` bigint unsigned NOT NULL COMMENT '附件大小（字节）',
  `file_type` tinyint unsigned NOT NULL COMMENT '附件类型（0: 文件, 1: 超大文件, 2: 图片）',
  `file_path` varchar(255) NOT NULL COMMENT '附件存储路径',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '附件上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `batcher_init_config`
--

DROP TABLE IF EXISTS `batcher_init_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `batcher_init_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '批号器配置ID',
  `batch_size` int unsigned DEFAULT NULL COMMENT '每批次邮件的数量',
  `interval` int unsigned DEFAULT NULL COMMENT '批次发送的时间间隔（单位：秒）',
  `start_time` bigint DEFAULT NULL COMMENT '发送开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '发送结束时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='批号器初始化配置管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_country`
--

DROP TABLE IF EXISTS `company_country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company_country` (
  `company_id` int unsigned NOT NULL COMMENT '公司ID',
  `country_id` int unsigned NOT NULL COMMENT '国家ID',
  `relation_type` tinyint unsigned NOT NULL COMMENT '公司与国家的关系类型(''branch'', ''office'', ''subsidiary'', ''other'')',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`company_id`,`country_id`) COMMENT '组合主键，确保公司与国家关系唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公司与国家关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_in`
--

DROP TABLE IF EXISTS `company_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company_in` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '公司id',
  `name` varchar(255) DEFAULT NULL COMMENT '公司名称',
  `name_english` varchar(255) DEFAULT NULL COMMENT '公司名称（英文）',
  `address` varchar(255) DEFAULT NULL COMMENT '公司地址',
  `address_english` varchar(255) DEFAULT NULL COMMENT '公司地址_english',
  `phone` varchar(255) DEFAULT NULL COMMENT '公司电话',
  `country_id` int unsigned DEFAULT NULL COMMENT '所属国家（id）',
  `country_name` varchar(255) DEFAULT NULL COMMENT '所属国家（name）',
  `country_name_english` varchar(255) DEFAULT NULL COMMENT '所属国家（name_english）',
  `region_id` tinyint unsigned DEFAULT NULL COMMENT '所属区域（id）',
  `region_name` varchar(255) DEFAULT NULL COMMENT '所属区域（name）',
  `region_name_english` varchar(255) DEFAULT NULL COMMENT '所属区域（name_english）',
  `parent_company_id` int unsigned DEFAULT NULL COMMENT '母公司（自引用，NULL 表示顶层公司）',
  `is_headquarter` tinyint unsigned DEFAULT NULL COMMENT '是否为总部',
  `description` text COMMENT '公司描述',
  `description_english` text COMMENT '公司描述_english',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公司_in';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_out`
--

DROP TABLE IF EXISTS `company_out`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company_out` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '公司id',
  `name` varchar(255) DEFAULT NULL COMMENT '公司名称',
  `name_english` varchar(255) DEFAULT NULL COMMENT '公司名称（英文）',
  `address` varchar(255) DEFAULT NULL COMMENT '公司地址',
  `address_english` varchar(255) DEFAULT NULL COMMENT '公司地址_english',
  `phone` varchar(255) DEFAULT NULL COMMENT '公司电话',
  `country_id` int unsigned DEFAULT NULL COMMENT '所属国家（id）',
  `country_name` varchar(255) DEFAULT NULL COMMENT '所属国家（name）',
  `country_name_english` varchar(255) DEFAULT NULL COMMENT '所属国家（name_english）',
  `region_id` tinyint unsigned DEFAULT NULL COMMENT '所属区域（id）',
  `region_name` varchar(255) DEFAULT NULL COMMENT '所属区域（name）',
  `region_name_english` varchar(255) DEFAULT NULL COMMENT '所属区域（name_english）',
  `description` text COMMENT '公司描述',
  `description_english` text COMMENT '公司描述_english',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公司_out';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `country`
--

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country` (
  `country_id` int unsigned NOT NULL COMMENT '国家ID',
  `country_name` varchar(255) DEFAULT NULL COMMENT '国家名称',
  `country_name_english` varchar(255) DEFAULT NULL COMMENT '国家英文名称',
  `country_code` varchar(255) DEFAULT NULL COMMENT '国际代码（如：CN、US）',
  `phone_code` int unsigned DEFAULT NULL COMMENT '电话代码 表示范围',
  `region_id` tinyint unsigned DEFAULT NULL COMMENT '所属区域ID',
  `region_name` varchar(255) DEFAULT NULL COMMENT '所属区域名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`country_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='国家表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '客户id',
  `name` varchar(255) DEFAULT NULL COMMENT '客户名称',
  `country_id` int unsigned DEFAULT NULL COMMENT '所属国家, 存储国家id',
  `country_name` varchar(255) DEFAULT NULL COMMENT '所属国家,存储国家name',
  `user_id` int unsigned DEFAULT NULL COMMENT '所属用户ID',
  `trade_type` tinyint unsigned DEFAULT NULL COMMENT '贸易类型（1：工厂，2：贸易商, 3:...）',
  `contact_persons_name` varchar(255) DEFAULT NULL COMMENT '联系人名字',
  `emails` json DEFAULT NULL COMMENT '多个邮箱（JSON格式存储）',
  `phone_numbers` varchar(255) DEFAULT NULL COMMENT '联系方式（电话）',
  `gender` tinyint unsigned DEFAULT NULL COMMENT '性别（0：男，1：女）',
  `birthday` bigint unsigned DEFAULT NULL COMMENT '生日, 时间戳形式存储',
  `level` tinyint unsigned DEFAULT NULL COMMENT '客户等级',
  `is_accept_marketing` tinyint unsigned DEFAULT NULL COMMENT '是否接受营销邮件,0:不接收 1:接收',
  `raw_materials` json DEFAULT NULL COMMENT '原料产品（JSON格式存储商品ID列表）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department_in`
--

DROP TABLE IF EXISTS `department_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_in` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `name` varchar(255) DEFAULT NULL COMMENT '部门名称',
  `company_id` int unsigned DEFAULT NULL COMMENT '所属公司ID',
  `manager_id` int unsigned DEFAULT NULL COMMENT '部门经理id',
  `manager_name` varchar(255) DEFAULT NULL COMMENT '部门经理姓名',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门_in';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department_out`
--

DROP TABLE IF EXISTS `department_out`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department_out` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `name` varchar(255) DEFAULT NULL COMMENT '部门名称',
  `company_id` int unsigned DEFAULT NULL COMMENT '所属公司ID',
  `manager_id` int unsigned DEFAULT NULL COMMENT '部门经理id',
  `manager_name` varchar(255) DEFAULT NULL COMMENT '部门经理姓名',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门_out';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email`
--

DROP TABLE IF EXISTS `email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '邮件唯一标识',
  `sender_id` int unsigned NOT NULL COMMENT '发件人 ID',
  `receiver_id` int unsigned NOT NULL COMMENT '收件人 ID',
  `subject` varchar(255) DEFAULT NULL COMMENT '邮件主题',
  `content` text COMMENT '邮件正文内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '邮件发送时间',
  `status` tinyint unsigned DEFAULT '0' COMMENT '邮件状态（0: 未读, 1: 已读, 2: 删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='邮件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_agent_config`
--

DROP TABLE IF EXISTS `email_agent_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_agent_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '邮件代理配置ID',
  `agent_name` varchar(255) DEFAULT NULL COMMENT '代理名称',
  `quota` int unsigned DEFAULT NULL COMMENT '代理配额（如每日最大邮件发送数量）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发送邮件Agent初始化配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_image`
--

DROP TABLE IF EXISTS `email_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_image` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '图片唯一标识',
  `attachment_id` bigint unsigned NOT NULL COMMENT '所属附件的 ID',
  `resolution` varchar(255) NOT NULL COMMENT '图片分辨率（例如: 1920x1080）',
  `format` varchar(255) NOT NULL COMMENT '图片格式（例如: jpeg, png）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '图片上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `large_file`
--

DROP TABLE IF EXISTS `large_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `large_file` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '超大文件唯一标识',
  `attachment_id` bigint unsigned NOT NULL COMMENT '所属附件的 ID',
  `chunk_count` int unsigned NOT NULL COMMENT '分片数量',
  `chunk_size` bigint unsigned NOT NULL COMMENT '每个分片大小（字节）',
  `total_size` bigint unsigned NOT NULL COMMENT '总文件大小',
  `upload_status` tinyint unsigned NOT NULL COMMENT '上传状态（0: 上传中, 1: 完成）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '超大文件上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='超大文件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mail_server_config`
--

DROP TABLE IF EXISTS `mail_server_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mail_server_config` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '邮件服务器配置ID',
  `server_name` varchar(255) DEFAULT NULL COMMENT '服务器名称',
  `server_ip` varchar(255) DEFAULT NULL COMMENT '服务器IP地址',
  `interval_time` int unsigned DEFAULT NULL COMMENT '发送邮件的时间间隔（单位：秒）',
  `total_time` int unsigned DEFAULT NULL COMMENT '邮件总发送时间（单位：秒）',
  `max_emails` int unsigned DEFAULT NULL COMMENT '受体服务器的最大邮件处理数量',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='受体邮件服务器管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mass_send_task`
--

DROP TABLE IF EXISTS `mass_send_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mass_send_task` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '群发任务ID',
  `strategy_id` int unsigned DEFAULT NULL COMMENT '使用的策略模板ID',
  `status` tinyint unsigned DEFAULT NULL COMMENT '任务状态（0: 待发送, 1: 发送中, 2: 完成）',
  `send_time` bigint unsigned DEFAULT NULL COMMENT '定时发送的时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群发任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `permission_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(255) DEFAULT NULL COMMENT '权限名称',
  `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `position_in`
--

DROP TABLE IF EXISTS `position_in`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `position_in` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '岗位id',
  `department_id` int unsigned DEFAULT NULL COMMENT '所属部门ID',
  `name` varchar(255) DEFAULT NULL COMMENT '岗位名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位_in';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `position_out`
--

DROP TABLE IF EXISTS `position_out`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `position_out` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '岗位id',
  `department_id` int unsigned DEFAULT NULL COMMENT '所属部门ID',
  `name` varchar(255) DEFAULT NULL COMMENT '岗位名称',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位_out';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `category_id` int unsigned DEFAULT NULL COMMENT '所属分类ID',
  `brand` varchar(255) DEFAULT NULL COMMENT '品牌名称',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '商品价格',
  `discount_price` decimal(10,2) DEFAULT NULL COMMENT '折扣价格',
  `stock` int unsigned DEFAULT '0' COMMENT '库存数量',
  `status` tinyint unsigned DEFAULT '1' COMMENT '商品状态（1：上架，0：下架）',
  `image_url` varchar(255) DEFAULT NULL COMMENT '主图URL',
  `gallery` json DEFAULT NULL COMMENT '图片集（JSON格式）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(255) DEFAULT NULL COMMENT '分类名称',
  `parent_id` int unsigned DEFAULT NULL COMMENT '父分类ID（顶级分类的parent_id为NULL）',
  `description` text COMMENT '分类描述',
  `image_url` varchar(255) DEFAULT NULL COMMENT '分类图片URL',
  `status` tinyint unsigned DEFAULT NULL COMMENT '状态（1：启用，0：禁用）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_company`
--

DROP TABLE IF EXISTS `product_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_company` (
  `product_id` int unsigned NOT NULL COMMENT '商品ID',
  `company_id` int unsigned NOT NULL COMMENT '公司ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`,`company_id`) COMMENT '组合主键，确保商品和公司的关系唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品与公司关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_customer`
--

DROP TABLE IF EXISTS `product_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_customer` (
  `product_id` int unsigned NOT NULL COMMENT '商品ID',
  `customer_id` int unsigned NOT NULL COMMENT '客户ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`,`customer_id`) COMMENT '组合主键，确保商品与客户的关系唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品与客户关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_supplier`
--

DROP TABLE IF EXISTS `product_supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_supplier` (
  `product_id` int unsigned NOT NULL COMMENT '商品ID',
  `supplier_id` int unsigned NOT NULL COMMENT '供应商ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`,`supplier_id`) COMMENT '组合主键，确保商品与供应商的关系唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品与供应商关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product_user`
--

DROP TABLE IF EXISTS `product_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_user` (
  `product_id` int unsigned NOT NULL COMMENT '商品ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`,`user_id`) COMMENT '组合主键，确保商品和用户的关系唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品与用户关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `region` (
  `region_id` tinyint unsigned NOT NULL COMMENT '区域ID',
  `region_name` varchar(255) DEFAULT NULL COMMENT '区域名称',
  `region_name_english` varchar(255) DEFAULT NULL COMMENT '区域英文名称',
  `description` text COMMENT '区域描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`region_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='区域表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `strategy_template`
--

DROP TABLE IF EXISTS `strategy_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `strategy_template` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '策略模板ID',
  `name` varchar(255) DEFAULT NULL COMMENT '模板名称',
  `type` tinyint unsigned DEFAULT NULL COMMENT '策略类型（1: 手动, 2: 自动）',
  `frequency_interval` int unsigned DEFAULT NULL COMMENT '群发频率间隔（单位：秒）',
  `max_messages` int unsigned DEFAULT NULL COMMENT '单位时间内的总消息上限',
  `is_timed` tinyint unsigned DEFAULT NULL COMMENT '是否定时发送（0: 否, 1: 是）',
  `stop_at` bigint unsigned DEFAULT NULL COMMENT '终止发送时间',
  `is_reply_forward` tinyint unsigned DEFAULT NULL COMMENT '是否启用回复转发（0: 否, 1: 是）',
  `is_holiday` tinyint unsigned DEFAULT NULL COMMENT '是否节日群发（0: 否, 1: 是）',
  `is_birthday` tinyint unsigned DEFAULT NULL COMMENT '是否生日群发（0: 否, 1: 是）',
  `holiday_details` json DEFAULT NULL COMMENT '节日详情（json格式）',
  `birthday_details` json DEFAULT NULL COMMENT '生日详情（json格式）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='群发策略模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `supplier`
--

DROP TABLE IF EXISTS `supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '供应商id',
  `name` varchar(255) DEFAULT NULL COMMENT '供应商名称',
  `country_id` int unsigned DEFAULT NULL COMMENT '所属国家, 存储国家id',
  `country_name` varchar(255) DEFAULT NULL COMMENT '所属国家,存储国家name',
  `user_id` int unsigned DEFAULT NULL COMMENT '所属用户ID',
  `trade_type` tinyint unsigned DEFAULT NULL COMMENT '贸易类型（1：工厂，2：贸易商 3:...）',
  `contact_persons_name` varchar(255) DEFAULT NULL COMMENT '联系人名字',
  `emails` json DEFAULT NULL COMMENT '多个邮箱（JSON格式存储）',
  `phone_numbers` json DEFAULT NULL COMMENT '联系方式（电话）',
  `gender` tinyint unsigned DEFAULT NULL COMMENT '性别（0：男，1：女）',
  `birthday` bigint unsigned DEFAULT NULL COMMENT '生日, 时间戳形式存储',
  `level` tinyint unsigned DEFAULT NULL COMMENT '供应商等级',
  `is_accept_marketing` tinyint unsigned DEFAULT NULL COMMENT '是否接受营销邮件,0:不接收 1:接收',
  `business_scope` json DEFAULT NULL COMMENT '经营范围（JSON格式存储多个商品id）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='供应商信息管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `undelivered_email`
--

DROP TABLE IF EXISTS `undelivered_email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `undelivered_email` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '未送达记录ID',
  `email_id` int unsigned DEFAULT NULL COMMENT '邮件ID',
  `recipient_id` int unsigned DEFAULT NULL COMMENT '收件人ID',
  `error_code` int unsigned DEFAULT NULL COMMENT '错误代码',
  `error_message` text COMMENT '错误信息描述',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='未送达管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `company_id` int unsigned DEFAULT NULL COMMENT '所属公司ID',
  `department_id` int unsigned DEFAULT NULL COMMENT '所属部门ID',
  `position_id` int unsigned DEFAULT NULL COMMENT '所属岗位ID',
  `role_id` int unsigned DEFAULT NULL COMMENT '角色id',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色name',
  `role_name_english` varchar(255) DEFAULT NULL COMMENT '角色name_english',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `password` varchar(255) DEFAULT NULL COMMENT '密码（加密存储）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `z_auto_strategy`
--

DROP TABLE IF EXISTS `z_auto_strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `z_auto_strategy` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '扩展策略ID',
  `strategy_id` int unsigned DEFAULT NULL COMMENT '关联策略模板的ID',
  `is_holiday` tinyint unsigned DEFAULT NULL COMMENT '是否节日群发（0: 否, 1: 是）',
  `is_birthday` tinyint unsigned DEFAULT NULL COMMENT '是否生日群发（0: 否, 1: 是）',
  `holiday_details` json DEFAULT NULL COMMENT '节日详情（JSON格式）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='自动策略扩展表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `z_role`
--

DROP TABLE IF EXISTS `z_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `z_role` (
  `role_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `role_name_english` varchar(255) DEFAULT NULL COMMENT '角色名称english',
  `description` text COMMENT '角色描述',
  `description_english` text COMMENT '角色描述_english',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `z_role_permission`
--

DROP TABLE IF EXISTS `z_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `z_role_permission` (
  `role_id` int unsigned NOT NULL COMMENT '角色ID',
  `permission_id` int unsigned NOT NULL COMMENT '权限ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`,`permission_id`) COMMENT '组合主键，确保每个角色与权限的关系唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `z_user_bak`
--

DROP TABLE IF EXISTS `z_user_bak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `z_user_bak` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '用户id_bak',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名_bak',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱_bak',
  `password` varchar(255) DEFAULT NULL COMMENT '密码_bak（加密存储）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户_bak';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-26 10:19:13
