CREATE DATABASE  IF NOT EXISTS `backgammon`;
USE `backgammon`;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,

  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

--
-- Table structure for table `book_publisher`
--

DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `book_publisher` (
  `book_id` int(10) unsigned NOT NULL,
  `publisher_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`book_id`,`publisher_id`),
  KEY `fk_bookpublisher_publisher_idx` (`publisher_id`),
  CONSTRAINT `fk_bookpublisher_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_bookpublisher_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

INSERT INTO account (
 email, first_name, last_name, password, rating )
VALUES
 ( "ronaldo@cristiano.com", "ronaldo", "cristiano", "password", 1500),
 ( "messi@lionel.com", "lionel", "messi", "password", 1500),
 ( "mohamed@salah.com", "mohamed", "salah", "password", 1500),
 ( "gereth@bale.com", "gereth", "bale", "password", 1500),
 ( "alexis@sanchez.com", "alexis", "sanchez", "password", 1500);

insert into account_role (account_id,role_id) values (5,5),(6,6),(7,7),(8,8),(9,9);

insert into role (name) values ("user"),("user"),("user"),("user"),("user");
