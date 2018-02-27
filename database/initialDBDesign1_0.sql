-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema mastermycourse
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mastermycourse
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mastermycourse` DEFAULT CHARACTER SET latin1 ;
USE `mastermycourse` ;

-- -----------------------------------------------------
-- Table `mastermycourse`.`ProgramLanguages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`ProgramLanguages` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`ProgramLanguages` (
  `id` INT(11) NOT NULL,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`CodeQuestionModule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`CodeQuestionModule` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`CodeQuestionModule` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `question` VARCHAR(10000) NOT NULL,
  `testCase` VARCHAR(10000) NOT NULL,
  `programLanguageId` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_CodeQuestionModuleProgramLanguageId_idx` (`programLanguageId` ASC),
  CONSTRAINT `fk_CodeQuestionModuleProgramLanguageId`
    FOREIGN KEY (`programLanguageId`)
    REFERENCES `mastermycourse`.`ProgramLanguages` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`Schools`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`Schools` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`Schools` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` CHAR(255) NOT NULL,
  `emailDomain` CHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `emailDomain_UNIQUE` (`emailDomain` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`Users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`Users` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`Users` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` CHAR(100) NOT NULL,
  `email` CHAR(255) NOT NULL,
  `status` INT(1) NOT NULL DEFAULT '1',
  `imageUrl` VARCHAR(524) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 34
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`Teachers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`Teachers` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`Teachers` (
  `userId` INT(11) NOT NULL,
  `approved` TINYINT(1) NOT NULL DEFAULT '0',
  `schoolId` INT(11) NULL DEFAULT NULL,
  `schoolName` CHAR(255) NOT NULL,
  `joinRequestDescription` BLOB NOT NULL,
  PRIMARY KEY (`userId`),
  INDEX `fk_TeachersSchoolId_idx` (`schoolId` ASC),
  CONSTRAINT `fk_TeachersSchoolId`
    FOREIGN KEY (`schoolId`)
    REFERENCES `mastermycourse`.`Schools` (`id`)
    ON UPDATE CASCADE,
  CONSTRAINT `fk_TeachersUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `mastermycourse`.`Users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`Courses`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`Courses` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`Courses` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` CHAR(255) NOT NULL,
  `teacherId` INT(11) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT '1',
  `isPublic` TINYINT(1) NOT NULL DEFAULT '0',
  `courseCode` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC),
  INDEX `fk_CoursesTeacherId_idx` (`teacherId` ASC),
  CONSTRAINT `fk_CoursesTeacherId`
    FOREIGN KEY (`teacherId`)
    REFERENCES `mastermycourse`.`Teachers` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 35
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`ContentModules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`ContentModules` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`ContentModules` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `courseId` INT(11) NOT NULL,
  `style` MEDIUMBLOB NOT NULL,
  `body` LONGBLOB NOT NULL,
  `title` CHAR(100) NOT NULL,
  `enabled` TINYINT(1) NOT NULL DEFAULT '1',
  `chapterTitle` CHAR(100) NOT NULL,
  `quizId` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fkContentModules_idx` (`courseId` ASC),
  CONSTRAINT `fkContentModules`
    FOREIGN KEY (`courseId`)
    REFERENCES `mastermycourse`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 17809
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`ContentModuleAudio`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`ContentModuleAudio` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`ContentModuleAudio` (
  `contentModuleId` INT(11) NOT NULL,
  `rawText` VARCHAR(20000) NOT NULL,
  `audio` LONGBLOB NULL DEFAULT NULL,
  PRIMARY KEY (`contentModuleId`),
  CONSTRAINT `fk_CMAContentModuleId`
    FOREIGN KEY (`contentModuleId`)
    REFERENCES `mastermycourse`.`ContentModules` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`QuizTestModule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`QuizTestModule` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`QuizTestModule` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `title` CHAR(125) NOT NULL,
  `courseId` INT(11) NOT NULL,
  `teacherNotes` VARCHAR(10000) NULL DEFAULT NULL,
  `submissionDate` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_QuizTestModuleCourseId_idx` (`courseId` ASC),
  CONSTRAINT `fk_QuizTestModuleCourseId`
    FOREIGN KEY (`courseId`)
    REFERENCES `mastermycourse`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 52
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`CourseOutline`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`CourseOutline` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`CourseOutline` (
  `courseId` INT(11) NOT NULL,
  `contentModuleId` INT(11) NULL DEFAULT NULL,
  `quizTestModuleId` INT(11) NULL DEFAULT NULL,
  `orderIndex` INT(11) NOT NULL,
  PRIMARY KEY (`courseId`, `orderIndex`),
  INDEX `fk_CourseOutlineContentModuleId_idx` (`contentModuleId` ASC),
  INDEX `fk_CourseOutlineQuizTestModuleId_idx` (`quizTestModuleId` ASC),
  CONSTRAINT `fk_CourseOutlineContentModuleId`
    FOREIGN KEY (`contentModuleId`)
    REFERENCES `mastermycourse`.`ContentModules` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_CourseOutlineCourseId`
    FOREIGN KEY (`courseId`)
    REFERENCES `mastermycourse`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_CourseOutlineQuizTestModuleId`
    FOREIGN KEY (`quizTestModuleId`)
    REFERENCES `mastermycourse`.`QuizTestModule` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`CourseTAs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`CourseTAs` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`CourseTAs` (
  `userId` INT(11) NOT NULL,
  `courseId` INT(11) NOT NULL,
  PRIMARY KEY (`userId`, `courseId`),
  INDEX `fk_CourseTAsCourseId_idx` (`courseId` ASC),
  CONSTRAINT `fk_CourseTAsCourseId`
    FOREIGN KEY (`courseId`)
    REFERENCES `mastermycourse`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_CourseTAsUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `mastermycourse`.`Users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`ExactAnswerModule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`ExactAnswerModule` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`ExactAnswerModule` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `question` VARCHAR(10000) NOT NULL,
  `answer` VARCHAR(10000) NOT NULL,
  `percentError` DOUBLE NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`MultipleChoiceModules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`MultipleChoiceModules` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`MultipleChoiceModules` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `question` VARCHAR(10000) NOT NULL,
  `correctAnswer` VARCHAR(2000) NOT NULL,
  `wrongAnswer1` VARCHAR(2000) NOT NULL,
  `wrongAnswer2` VARCHAR(2000) NOT NULL,
  `wrongAnswer3` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`OversightQuestionModule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`OversightQuestionModule` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`OversightQuestionModule` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `question` VARCHAR(10000) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 22
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`TrueFalseModule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`TrueFalseModule` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`TrueFalseModule` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `question` VARCHAR(10000) NOT NULL,
  `answer` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`QuizTestModuleQuestions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`QuizTestModuleQuestions` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`QuizTestModuleQuestions` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `multipleChoiceModuleId` INT(11) NULL DEFAULT NULL,
  `codeQuestionModuleId` INT(11) NULL DEFAULT NULL,
  `trueFalseModuleId` INT(11) NULL DEFAULT NULL,
  `exactAnswerModuleId` INT(11) NULL DEFAULT NULL,
  `oversightQuestionModule` INT(11) NULL DEFAULT NULL,
  `courseId` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_QuizTestModuleQuestionCourseId_idx` (`courseId` ASC),
  INDEX `fk_QTMQMultipleChoiceModuleId_idx` (`multipleChoiceModuleId` ASC),
  INDEX `fk_QTMQCodeQuestionModuleId_idx` (`codeQuestionModuleId` ASC),
  INDEX `fk_QTCMTrueFalseModuleId_idx` (`trueFalseModuleId` ASC),
  INDEX `fk_QTCMExactAnswerModuleId_idx` (`exactAnswerModuleId` ASC),
  INDEX `fk_QTCMOversightQuestionModuleId_idx` (`oversightQuestionModule` ASC),
  CONSTRAINT `fk_QTCMExactAnswerModuleId`
    FOREIGN KEY (`exactAnswerModuleId`)
    REFERENCES `mastermycourse`.`ExactAnswerModule` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_QTCMOversightQuestionModuleId`
    FOREIGN KEY (`oversightQuestionModule`)
    REFERENCES `mastermycourse`.`OversightQuestionModule` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_QTCMTrueFalseModuleId`
    FOREIGN KEY (`trueFalseModuleId`)
    REFERENCES `mastermycourse`.`TrueFalseModule` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_QTMQCodeQuestionModuleId`
    FOREIGN KEY (`codeQuestionModuleId`)
    REFERENCES `mastermycourse`.`CodeQuestionModule` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_QTMQMultipleChoiceModuleId`
    FOREIGN KEY (`multipleChoiceModuleId`)
    REFERENCES `mastermycourse`.`MultipleChoiceModules` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_QuizTestModuleQuestionCourseId`
    FOREIGN KEY (`courseId`)
    REFERENCES `mastermycourse`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 54
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`QuizTestOrder`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`QuizTestOrder` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`QuizTestOrder` (
  `quizTestModuleId` INT(11) NOT NULL,
  `quizTestModuleQuestionId` INT(11) NOT NULL,
  `orderIndex` INT(11) NOT NULL,
  `totalSubmissions` INT(11) NOT NULL,
  `points` INT(11) NOT NULL,
  `question` VARCHAR(10000) NOT NULL,
  PRIMARY KEY (`quizTestModuleId`, `quizTestModuleQuestionId`),
  INDEX `fk_QuizTestOrderQuizTestModuleQuestionId_idx` (`quizTestModuleQuestionId` ASC),
  CONSTRAINT `fk_QuizTestOrderQuizTestModuleId`
    FOREIGN KEY (`quizTestModuleId`)
    REFERENCES `mastermycourse`.`QuizTestModule` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_QuizTestOrderQuizTestModuleQuestionId`
    FOREIGN KEY (`quizTestModuleQuestionId`)
    REFERENCES `mastermycourse`.`QuizTestModuleQuestions` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`StudentCourses`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`StudentCourses` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`StudentCourses` (
  `userId` INT(11) NOT NULL,
  `courseId` INT(11) NOT NULL,
  `approved` TINYINT(1) NOT NULL DEFAULT '0',
  `lastLoggedInTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`, `courseId`),
  INDEX `fk_StudentCoursesCourseId_idx` (`courseId` ASC),
  CONSTRAINT `fk_StudentCoursesCourseId`
    FOREIGN KEY (`courseId`)
    REFERENCES `mastermycourse`.`Courses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_StudentCoursesUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `mastermycourse`.`Users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`StudentAnswers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`StudentAnswers` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`StudentAnswers` (
  `userId` INT(11) NOT NULL,
  `quizTestModuleId` INT(11) NOT NULL,
  `quizTestModuleQuestionId` INT(11) NOT NULL,
  `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `answer` VARCHAR(2000) NOT NULL,
  `isCorrect` TINYINT(1) NOT NULL,
  `teacherComment` VARCHAR(2000) NOT NULL,
  `isGraded` TINYINT(1) NOT NULL,
  PRIMARY KEY (`userId`, `quizTestModuleId`, `quizTestModuleQuestionId`),
  INDEX `fk_StudentAnswersQuizTestModuleId_idx` (`quizTestModuleId` ASC),
  INDEX `fk_StudentAnswerQuizQuestionModuleId_idx` (`quizTestModuleQuestionId` ASC),
  CONSTRAINT `fk_StudentAnswerQuizQuestionModuleId`
    FOREIGN KEY (`quizTestModuleQuestionId`)
    REFERENCES `mastermycourse`.`QuizTestOrder` (`quizTestModuleQuestionId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_StudentAnswersQuizTestModuleId`
    FOREIGN KEY (`quizTestModuleId`)
    REFERENCES `mastermycourse`.`QuizTestOrder` (`quizTestModuleId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_StudentAnswersUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `mastermycourse`.`StudentCourses` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`StudentContentHighlighting`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`StudentContentHighlighting` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`StudentContentHighlighting` (
  `userId` INT(11) NOT NULL,
  `startWordId` CHAR(255) NOT NULL,
  `endWordId` CHAR(255) NOT NULL,
  `contentModuleId` INT(11) NOT NULL,
  PRIMARY KEY (`userId`, `contentModuleId`, `endWordId`, `startWordId`),
  INDEX `fk_SCHContentModuleId_idx` (`contentModuleId` ASC),
  CONSTRAINT `fk_SCHContentModuleId`
    FOREIGN KEY (`contentModuleId`)
    REFERENCES `mastermycourse`.`ContentModules` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_SCHUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `mastermycourse`.`StudentCourses` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`StudentContentMetrics`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`StudentContentMetrics` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`StudentContentMetrics` (
  `userId` INT(11) NOT NULL,
  `contentModuleId` INT(11) NOT NULL,
  `secondsSpentReadingContent` INT(11) NOT NULL,
  `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`, `contentModuleId`),
  INDEX `fk_SCMContentModuleId_idx` (`contentModuleId` ASC),
  CONSTRAINT `fk_SCMContentModuleId`
    FOREIGN KEY (`contentModuleId`)
    REFERENCES `mastermycourse`.`ContentModules` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_SCMUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `mastermycourse`.`StudentCourses` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `mastermycourse`.`StudentNotes`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mastermycourse`.`StudentNotes` ;

CREATE TABLE IF NOT EXISTS `mastermycourse`.`StudentNotes` (
  `userId` INT(11) NOT NULL,
  `courseId` INT(11) NOT NULL,
  `notes` VARCHAR(10000) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`userId`, `courseId`),
  INDEX `fk_StudentNotesCourseId_idx` (`courseId` ASC),
  CONSTRAINT `fk_StudentNotesCourseId`
    FOREIGN KEY (`courseId`)
    REFERENCES `mastermycourse`.`StudentCourses` (`courseId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_StudentNotesUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `mastermycourse`.`StudentCourses` (`userId`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
