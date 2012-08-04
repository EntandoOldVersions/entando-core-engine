-- MySQL dump 10.13  Distrib 5.5.24, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: EntandoCoreEnginetestServ
-- ------------------------------------------------------
-- Server version	5.5.24-0ubuntu0.12.04.1

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
-- Current Database: `EntandoCoreEnginetestServ`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `EntandoCoreEnginetestServ` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `EntandoCoreEnginetestServ`;

--
-- Table structure for table `api_oauth_consumers`
--

DROP TABLE IF EXISTS `api_oauth_consumers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_oauth_consumers` (
  `consumerkey` varchar(100) NOT NULL,
  `consumersecret` varchar(100) NOT NULL,
  `description` varchar(500) NOT NULL,
  `callbackurl` varchar(500) DEFAULT NULL,
  `expirationdate` date DEFAULT NULL,
  PRIMARY KEY (`consumerkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_oauth_consumers`
--

LOCK TABLES `api_oauth_consumers` WRITE;
/*!40000 ALTER TABLE `api_oauth_consumers` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_oauth_consumers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api_oauth_tokens`
--

DROP TABLE IF EXISTS `api_oauth_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `api_oauth_tokens` (
  `accesstoken` varchar(100) NOT NULL,
  `tokensecret` varchar(100) NOT NULL,
  `consumerkey` varchar(500) NOT NULL,
  `lastaccess` date DEFAULT NULL,
  `username` varchar(40) NOT NULL,
  PRIMARY KEY (`accesstoken`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_oauth_tokens`
--

LOCK TABLES `api_oauth_tokens` WRITE;
/*!40000 ALTER TABLE `api_oauth_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_oauth_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apicatalog_methods`
--

DROP TABLE IF EXISTS `apicatalog_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apicatalog_methods` (
  `resource` varchar(100) NOT NULL,
  `httpmethod` varchar(6) NOT NULL,
  `isactive` tinyint(4) DEFAULT NULL,
  `authenticationrequired` tinyint(4) DEFAULT NULL,
  `authorizationrequired` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`resource`,`httpmethod`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apicatalog_methods`
--

LOCK TABLES `apicatalog_methods` WRITE;
/*!40000 ALTER TABLE `apicatalog_methods` DISABLE KEYS */;
/*!40000 ALTER TABLE `apicatalog_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apicatalog_services`
--

DROP TABLE IF EXISTS `apicatalog_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `apicatalog_services` (
  `servicekey` varchar(100) NOT NULL,
  `resource` varchar(100) DEFAULT NULL,
  `description` longtext NOT NULL,
  `parameters` longtext,
  `tag` varchar(100) DEFAULT NULL,
  `freeparameters` longtext,
  `isactive` tinyint(4) NOT NULL,
  `ispublic` tinyint(4) NOT NULL,
  `myentando` tinyint(4) NOT NULL,
  PRIMARY KEY (`servicekey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apicatalog_services`
--

LOCK TABLES `apicatalog_services` WRITE;
/*!40000 ALTER TABLE `apicatalog_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `apicatalog_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authgroups`
--

DROP TABLE IF EXISTS `authgroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authgroups` (
  `groupname` varchar(20) NOT NULL,
  `descr` varchar(50) NOT NULL,
  PRIMARY KEY (`groupname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authgroups`
--

LOCK TABLES `authgroups` WRITE;
/*!40000 ALTER TABLE `authgroups` DISABLE KEYS */;
INSERT INTO `authgroups` VALUES ('administrators','Amministratori'),('coach','Coach'),('customers','Customers'),('free','Accesso Libero'),('helpdesk','Helpdesk'),('management','Management');
/*!40000 ALTER TABLE `authgroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authpermissions`
--

DROP TABLE IF EXISTS `authpermissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authpermissions` (
  `permissionname` varchar(30) NOT NULL,
  `descr` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`permissionname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authpermissions`
--

LOCK TABLES `authpermissions` WRITE;
/*!40000 ALTER TABLE `authpermissions` DISABLE KEYS */;
INSERT INTO `authpermissions` VALUES ('editContents','Redazione di Contenuti'),('enterBackend','Accesso all\'Area di Amministrazione'),('manageCategories','Operazioni su Categorie'),('managePages','Operazioni su Pagine'),('manageResources','Operazioni su Risorse'),('superuser','Tutte le funzioni'),('validateContents','Supervisione di Contenuti');
/*!40000 ALTER TABLE `authpermissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authrolepermissions`
--

DROP TABLE IF EXISTS `authrolepermissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authrolepermissions` (
  `rolename` varchar(30) NOT NULL,
  `permissionname` varchar(30) NOT NULL,
  PRIMARY KEY (`rolename`,`permissionname`),
  KEY `authrolepermissions_permissionname_fkey` (`permissionname`),
  CONSTRAINT `authrolepermissions_permissionname_fkey` FOREIGN KEY (`permissionname`) REFERENCES `authpermissions` (`permissionname`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `authrolepermissions_rolename_fkey` FOREIGN KEY (`rolename`) REFERENCES `authroles` (`rolename`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authrolepermissions`
--

LOCK TABLES `authrolepermissions` WRITE;
/*!40000 ALTER TABLE `authrolepermissions` DISABLE KEYS */;
INSERT INTO `authrolepermissions` VALUES ('editor','editContents'),('supervisor','editContents'),('editor','enterBackend'),('pageManager','enterBackend'),('supervisor','enterBackend'),('pageManager','managePages'),('editor','manageResources'),('admin','superuser'),('supervisor','validateContents');
/*!40000 ALTER TABLE `authrolepermissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authroles`
--

DROP TABLE IF EXISTS `authroles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authroles` (
  `rolename` varchar(30) NOT NULL,
  `descr` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`rolename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authroles`
--

LOCK TABLES `authroles` WRITE;
/*!40000 ALTER TABLE `authroles` DISABLE KEYS */;
INSERT INTO `authroles` VALUES ('admin','Tutte le funzioni'),('editor','Gestore di Contenuti e Risorse'),('pageManager','Gestore di Pagine'),('supervisor','Supervisore di Contenuti');
/*!40000 ALTER TABLE `authroles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authusergroups`
--

DROP TABLE IF EXISTS `authusergroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authusergroups` (
  `username` varchar(40) NOT NULL,
  `groupname` varchar(20) NOT NULL,
  PRIMARY KEY (`username`,`groupname`),
  KEY `new_fk_constraint` (`groupname`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`groupname`) REFERENCES `authgroups` (`groupname`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authusergroups`
--

LOCK TABLES `authusergroups` WRITE;
/*!40000 ALTER TABLE `authusergroups` DISABLE KEYS */;
INSERT INTO `authusergroups` VALUES ('admin','administrators'),('mainEditor','administrators'),('editorCoach','coach'),('pageManagerCoach','coach'),('supervisorCoach','coach'),('editorCoach','customers'),('editorCustomers','customers'),('pageManagerCoach','customers'),('pageManagerCustomers','customers'),('supervisorCoach','customers'),('supervisorCustomers','customers');
/*!40000 ALTER TABLE `authusergroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authuserroles`
--

DROP TABLE IF EXISTS `authuserroles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authuserroles` (
  `username` varchar(40) NOT NULL,
  `rolename` varchar(30) NOT NULL,
  PRIMARY KEY (`username`,`rolename`),
  KEY `authuserroles_rolename_fkey` (`rolename`),
  CONSTRAINT `authuserroles_rolename_fkey` FOREIGN KEY (`rolename`) REFERENCES `authroles` (`rolename`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authuserroles`
--

LOCK TABLES `authuserroles` WRITE;
/*!40000 ALTER TABLE `authuserroles` DISABLE KEYS */;
INSERT INTO `authuserroles` VALUES ('admin','admin'),('editorCoach','editor'),('editorCustomers','editor'),('mainEditor','editor'),('pageManagerCoach','pageManager'),('pageManagerCustomers','pageManager'),('supervisorCoach','supervisor'),('supervisorCustomers','supervisor');
/*!40000 ALTER TABLE `authuserroles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authusers`
--

DROP TABLE IF EXISTS `authusers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authusers` (
  `username` varchar(40) NOT NULL,
  `passwd` varchar(40) DEFAULT NULL,
  `registrationdate` date NOT NULL,
  `lastaccess` date DEFAULT NULL,
  `lastpasswordchange` date DEFAULT NULL,
  `active` tinyint(4) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authusers`
--

LOCK TABLES `authusers` WRITE;
/*!40000 ALTER TABLE `authusers` DISABLE KEYS */;
INSERT INTO `authusers` VALUES ('admin','admin','2008-09-25','2010-11-30',NULL,1),('editorCoach','editorCoach','2008-09-25','2010-11-30',NULL,1),('editorCustomers','editorCustomers','2008-09-25','2010-11-30',NULL,1),('mainEditor','mainEditor','2008-09-25','2010-11-30',NULL,1),('pageManagerCoach','pageManagerCoach','2008-09-25','2010-11-30',NULL,1),('pageManagerCustomers','pageManagerCustomers','2008-09-25','2010-11-30',NULL,1),('supervisorCoach','supervisorCoach','2008-09-25','2010-11-30',NULL,1),('supervisorCustomers','supervisorCustomers','2008-09-25','2010-11-30',NULL,1);
/*!40000 ALTER TABLE `authusers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authusershortcuts`
--

DROP TABLE IF EXISTS `authusershortcuts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authusershortcuts` (
  `username` varchar(40) NOT NULL,
  `config` longtext NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authusershortcuts`
--

LOCK TABLES `authusershortcuts` WRITE;
/*!40000 ALTER TABLE `authusershortcuts` DISABLE KEYS */;
INSERT INTO `authusershortcuts` VALUES ('admin','<shortcuts>\n	<box pos=\"0\"></box>\n	<box pos=\"1\">core.component.user.list</box>\n	<box pos=\"2\">jacms.content.new</box>\n	<box pos=\"3\">jacms.content.list</box>\n	<box pos=\"4\">core.portal.pageTree</box>\n	<box pos=\"5\">core.portal.showletType</box>\n	<box pos=\"6\">core.tools.setting</box>\n	<box pos=\"7\">core.tools.entities</box>\n</shortcuts>');
/*!40000 ALTER TABLE `authusershortcuts` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-08-04 17:01:24
