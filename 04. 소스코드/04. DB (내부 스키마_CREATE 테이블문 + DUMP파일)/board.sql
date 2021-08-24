-- --------------------------------------------------------
-- 호스트:                          52.78.69.167
-- 서버 버전:                        10.1.37-MariaDB - MariaDB Server
-- 서버 OS:                        Linux
-- HeidiSQL 버전:                  11.2.0.6213
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- test 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `test` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `test`;

-- 테이블 test.TBL_ATTACH 구조 내보내기
CREATE TABLE IF NOT EXISTS `TBL_ATTACH` (
  `IDX` int(11) NOT NULL COMMENT '게시물아이디',
  `SEQ` int(11) NOT NULL COMMENT '순번',
  `FILENAME` varchar(256) DEFAULT NULL COMMENT '파일명',
  `WRITER` varchar(50) DEFAULT NULL COMMENT '등록자',
  `INDATE` datetime DEFAULT NULL COMMENT '등록일',
  PRIMARY KEY (`IDX`,`SEQ`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 test.TBL_BOARD 구조 내보내기
CREATE TABLE IF NOT EXISTS `TBL_BOARD` (
  `IDX` int(11) NOT NULL AUTO_INCREMENT COMMENT '게시물아이디',
  `TITLE` varchar(300) DEFAULT NULL COMMENT '제목',
  `CONTENTS` varchar(4000) DEFAULT NULL COMMENT '내용',
  `COUNT` int(11) DEFAULT NULL COMMENT '조회수',
  `WRITER` varchar(50) DEFAULT NULL COMMENT '등록자',
  `INDATE` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
  PRIMARY KEY (`IDX`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 test.TBL_REPLY 구조 내보내기
CREATE TABLE IF NOT EXISTS `TBL_REPLY` (
  `IDX` int(11) NOT NULL COMMENT '게시물아이디',
  `SEQ` int(11) NOT NULL COMMENT '순번',
  `REPLY` varchar(1000) DEFAULT NULL COMMENT '댓글',
  `WRITER` varchar(50) DEFAULT NULL COMMENT '작성자',
  `INDATE` datetime DEFAULT NULL COMMENT '작성일',
  PRIMARY KEY (`IDX`,`SEQ`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 test.TBL_USERS 구조 내보내기
CREATE TABLE IF NOT EXISTS `TBL_USERS` (
  `USER_ID` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '아이디',
  `USER_NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이름',
  `PASSWORD` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '비밀번호',
  `USE_YN` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '사용여부'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
