-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.58-1ubuntu1


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema EntandotestServ
--

CREATE DATABASE IF NOT EXISTS EntandotestServ;
USE EntandotestServ;
CREATE TABLE  `EntandotestServ`.`api_oauth_consumers` (
  `consumerkey` varchar(100) NOT NULL,
  `consumersecret` varchar(100) NOT NULL,
  `description` varchar(500) NOT NULL,
  `callbackurl` varchar(500),
  `expirationdate` date DEFAULT NULL,
  PRIMARY KEY (`consumerkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE  `EntandotestServ`.`api_oauth_consumers` (
  `accesstoken` varchar(100) NOT NULL,
  `tokensecret` varchar(100) NOT NULL,
  `consumerkey` varchar(500) NOT NULL,
  `lastaccess` date DEFAULT NULL,
  `username` varchar(40) NOT NULL,
  PRIMARY KEY (`accesstoken`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE  `EntandotestServ`.`apicatalog_methods` (
  `resource` varchar(100) NOT NULL,
  `httpmethod` varchar(6) NOT NULL,
  `isactive` tinyint(4),
  `authenticationrequired` tinyint(4),
  `authorizationrequired` varchar(100),
  PRIMARY KEY (`resource`, `httpmethod`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE  `EntandotestServ`.`apicatalog_services` (
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
CREATE TABLE  `EntandotestServ`.`authgroups` (
  `groupname` varchar(20) NOT NULL,
  `descr` varchar(50) NOT NULL,
  PRIMARY KEY (`groupname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authgroups` VALUES  ('administrators','Amministratori'),
 ('coach','Coach'),
 ('customers','Customers'),
 ('free','Accesso Libero'),
 ('helpdesk','Helpdesk'),
 ('management','Management');
CREATE TABLE  `EntandotestServ`.`authpermissions` (
  `permissionname` varchar(30) NOT NULL,
  `descr` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`permissionname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authpermissions` VALUES  ('editContents','Redazione di Contenuti'),
 ('enterBackend','Accesso all\'Area di Amministrazione'),
 ('manageCategories','Operazioni su Categorie'),
 ('managePages','Operazioni su Pagine'),
 ('manageResources','Operazioni su Risorse'),
 ('superuser','Tutte le funzioni'),
 ('validateContents','Supervisione di Contenuti');
CREATE TABLE  `EntandotestServ`.`authrolepermissions` (
  `rolename` varchar(30) NOT NULL,
  `permissionname` varchar(30) NOT NULL,
  PRIMARY KEY (`rolename`,`permissionname`),
  KEY `authrolepermissions_permissionname_fkey` (`permissionname`),
  CONSTRAINT `authrolepermissions_permissionname_fkey` FOREIGN KEY (`permissionname`) REFERENCES `authpermissions` (`permissionname`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `authrolepermissions_rolename_fkey` FOREIGN KEY (`rolename`) REFERENCES `authroles` (`rolename`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authrolepermissions` VALUES  ('editor','editContents'),
 ('supervisor','editContents'),
 ('editor','enterBackend'),
 ('pageManager','enterBackend'),
 ('supervisor','enterBackend'),
 ('pageManager','managePages'),
 ('editor','manageResources'),
 ('admin','superuser'),
 ('supervisor','validateContents');
CREATE TABLE  `EntandotestServ`.`authroles` (
  `rolename` varchar(30) NOT NULL,
  `descr` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`rolename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authroles` VALUES  ('admin','Tutte le funzioni'),
 ('editor','Gestore di Contenuti e Risorse'),
 ('pageManager','Gestore di Pagine'),
 ('supervisor','Supervisore di Contenuti');
CREATE TABLE  `EntandotestServ`.`authusergroups` (
  `username` varchar(40) NOT NULL,
  `groupname` varchar(20) NOT NULL,
  PRIMARY KEY (`username`,`groupname`),
  KEY `new_fk_constraint` (`groupname`),
  CONSTRAINT `new_fk_constraint` FOREIGN KEY (`groupname`) REFERENCES `authgroups` (`groupname`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authusergroups` VALUES  ('admin','administrators'),
 ('mainEditor','administrators'),
 ('editorCoach','coach'),
 ('pageManagerCoach','coach'),
 ('supervisorCoach','coach'),
 ('editorCoach','customers'),
 ('editorCustomers','customers'),
 ('pageManagerCoach','customers'),
 ('pageManagerCustomers','customers'),
 ('supervisorCoach','customers'),
 ('supervisorCustomers','customers');
CREATE TABLE  `EntandotestServ`.`authuserroles` (
  `username` varchar(40) NOT NULL,
  `rolename` varchar(30) NOT NULL,
  PRIMARY KEY (`username`,`rolename`),
  KEY `authuserroles_rolename_fkey` (`rolename`),
  CONSTRAINT `authuserroles_rolename_fkey` FOREIGN KEY (`rolename`) REFERENCES `authroles` (`rolename`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authuserroles` VALUES  ('admin','admin'),
 ('editorCoach','editor'),
 ('editorCustomers','editor'),
 ('mainEditor','editor'),
 ('pageManagerCoach','pageManager'),
 ('pageManagerCustomers','pageManager'),
 ('supervisorCoach','supervisor'),
 ('supervisorCustomers','supervisor');
CREATE TABLE  `EntandotestServ`.`authusers` (
  `username` varchar(40) NOT NULL,
  `passwd` varchar(40) DEFAULT NULL,
  `registrationdate` date NOT NULL,
  `lastaccess` date DEFAULT NULL,
  `lastpasswordchange` date DEFAULT NULL,
  `active` tinyint(4) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authusers` VALUES  ('admin','admin','2008-09-25','2010-11-30',NULL,1),
 ('editorCoach','editorCoach','2008-09-25','2010-11-30',NULL,1),
 ('editorCustomers','editorCustomers','2008-09-25','2010-11-30',NULL,1),
 ('mainEditor','mainEditor','2008-09-25','2010-11-30',NULL,1),
 ('pageManagerCoach','pageManagerCoach','2008-09-25','2010-11-30',NULL,1),
 ('pageManagerCustomers','pageManagerCustomers','2008-09-25','2010-11-30',NULL,1),
 ('supervisorCoach','supervisorCoach','2008-09-25','2010-11-30',NULL,1),
 ('supervisorCustomers','supervisorCustomers','2008-09-25','2010-11-30',NULL,1);
CREATE TABLE  `EntandotestServ`.`authusershortcuts` (
  `username` varchar(40) NOT NULL,
  `config` longtext NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `EntandotestServ`.`authusershortcuts` VALUES  ('admin','<shortcuts>\n	<box pos=\"0\"></box>\n	<box pos=\"1\">core.component.user.list</box>\n	<box pos=\"2\">jacms.content.new</box>\n	<box pos=\"3\">jacms.content.list</box>\n	<box pos=\"4\">core.portal.pageTree</box>\n	<box pos=\"5\">core.portal.showletType</box>\n	<box pos=\"6\">core.tools.setting</box>\n	<box pos=\"7\">core.tools.entities</box>\n</shortcuts>');



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
