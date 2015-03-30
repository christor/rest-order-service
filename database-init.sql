-- MySQL dump 10.13  Distrib 5.6.23, for Linux (x86_64)
--
-- Host: localhost    Database: ordersystem
-- ------------------------------------------------------
-- Server version	5.6.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customer_order`
--

DROP TABLE IF EXISTS `customer_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `order_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_order`
--

LOCK TABLES `customer_order` WRITE;
/*!40000 ALTER TABLE `customer_order` DISABLE KEYS */;
INSERT INTO `customer_order` VALUES (1,'328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214','ralph','2015-03-30 15:55:07'),(2,'328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214','alice','2015-03-30 15:55:07'),(3,'328 Chauncey Street, Apartment 4B, Bensonhurst, NY 11214','ed','2015-03-30 15:55:07'),(4,'328 Chauncey Street, Apartment 3B, Bensonhurst, NY 11214','ralph','2015-03-30 15:55:07');
/*!40000 ALTER TABLE `customer_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_order_products`
--

DROP TABLE IF EXISTS `customer_order_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_order_products` (
  `customer_order_id` bigint(20) NOT NULL,
  `products_id` bigint(20) NOT NULL,
  KEY `FK_a858b4gsn81sy5o2pcp183l8e` (`products_id`),
  KEY `FK_b07xjf4pmwb4x0j0ewhb8a9h8` (`customer_order_id`),
  CONSTRAINT `FK_a858b4gsn81sy5o2pcp183l8e` FOREIGN KEY (`products_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FK_b07xjf4pmwb4x0j0ewhb8a9h8` FOREIGN KEY (`customer_order_id`) REFERENCES `customer_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_order_products`
--

LOCK TABLES `customer_order_products` WRITE;
/*!40000 ALTER TABLE `customer_order_products` DISABLE KEYS */;
INSERT INTO `customer_order_products` VALUES (1,1),(2,2),(3,1),(3,2),(4,1),(4,2),(4,2),(4,2),(4,2);
/*!40000 ALTER TABLE `customer_order_products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` int(11) NOT NULL,
  `stock` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'The first widget in our test set','Widget 1',100,10),(2,'The second widget in our test set','Widget 2',1000,100);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restock_order`
--

DROP TABLE IF EXISTS `restock_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `restock_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restock_order`
--

LOCK TABLES `restock_order` WRITE;
/*!40000 ALTER TABLE `restock_order` DISABLE KEYS */;
INSERT INTO `restock_order` VALUES (1,NULL),(2,NULL),(3,NULL),(4,NULL);
/*!40000 ALTER TABLE `restock_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `restock_order_products`
--

DROP TABLE IF EXISTS `restock_order_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `restock_order_products` (
  `restock_order_id` bigint(20) NOT NULL,
  `products_id` bigint(20) NOT NULL,
  KEY `FK_6xh89991f99i2saudsnbct3rp` (`products_id`),
  KEY `FK_4obdqwg5j5l3jv2cshie0isvy` (`restock_order_id`),
  CONSTRAINT `FK_4obdqwg5j5l3jv2cshie0isvy` FOREIGN KEY (`restock_order_id`) REFERENCES `restock_order` (`id`),
  CONSTRAINT `FK_6xh89991f99i2saudsnbct3rp` FOREIGN KEY (`products_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `restock_order_products`
--

LOCK TABLES `restock_order_products` WRITE;
/*!40000 ALTER TABLE `restock_order_products` DISABLE KEYS */;
INSERT INTO `restock_order_products` VALUES (1,1),(1,2),(2,1),(2,2),(3,1),(3,2),(4,1),(4,2);
/*!40000 ALTER TABLE `restock_order_products` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-03-30 19:56:08
