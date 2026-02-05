/*
 Navicat Premium Data Transfer

 Source Server         : 3306
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3306
 Source Schema         : microticket

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 01/02/2026 01:30:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cinemas
-- ----------------------------
DROP TABLE IF EXISTS `cinemas`;
CREATE TABLE `cinemas`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for halls
-- ----------------------------
DROP TABLE IF EXISTS `halls`;
CREATE TABLE `halls`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `cinema_id` bigint(0) NULL DEFAULT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `rows` int(0) NULL DEFAULT NULL,
  `cols` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for movies
-- ----------------------------
DROP TABLE IF EXISTS `movies`;
CREATE TABLE `movies`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `duration` int(0) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `poster_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `rating` decimal(2, 1) NULL DEFAULT NULL,
  `duration_minutes` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of movies
-- ----------------------------
INSERT INTO `movies` VALUES (1, '流浪地球2', 173, '科幻电影', 'https://example.com/a.jpg', 8.5, NULL);
INSERT INTO `movies` VALUES (2, '你的名字', 106, '动画电影', 'https://example.com/b.jpg', 8.8, NULL);

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NULL DEFAULT NULL,
  `seat_row` int(0) NULL DEFAULT NULL,
  `seat_col` int(0) NULL DEFAULT NULL,
  `seat_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `screening_id` bigint(0) NULL DEFAULT NULL,
  `total_amount` decimal(10, 2) NULL DEFAULT NULL,
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `paid_at` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD001', 2, 1, 88.00, 'PAID', '2026-01-31 22:46:04', NULL);
INSERT INTO `orders` VALUES (2, 'ORD002', 2, 1, 45.00, 'PAID', '2026-01-31 22:46:04', NULL);

-- ----------------------------
-- Table structure for payments
-- ----------------------------
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NULL DEFAULT NULL,
  `trade_no` varchar(128) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `amount` decimal(10, 2) NULL DEFAULT NULL,
  `status` varchar(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `platform` varchar(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `pay_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for screenings
-- ----------------------------
DROP TABLE IF EXISTS `screenings`;
CREATE TABLE `screenings`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `movie_id` bigint(0) NULL DEFAULT NULL,
  `hall_id` bigint(0) NULL DEFAULT NULL,
  `start_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `available_seats` int(0) NULL DEFAULT NULL,
  `total_seats` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seats
-- ----------------------------
DROP TABLE IF EXISTS `seats`;
CREATE TABLE `seats`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `hall_id` bigint(0) NULL DEFAULT NULL,
  `row_num` int(0) NULL DEFAULT NULL,
  `col_num` int(0) NULL DEFAULT NULL,
  `seat_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'normal',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_hall_row_col`(`hall_id`, `row_num`, `col_num`) USING BTREE,
  UNIQUE INDEX `UKt8ps1wrlqwmocum36pl9f3wg6`(`hall_id`, `row_num`, `col_num`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `nickname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `nickname_code` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `role` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `openid`(`openid`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  UNIQUE INDEX `nickname_code`(`nickname_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, NULL, 'admin', '0000', NULL, '2026-01-30 23:25:51', 'admin', 'admin123', 'ADMIN', NULL);

SET FOREIGN_KEY_CHECKS = 1;
