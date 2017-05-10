CREATE DATABASE test;

CREATE TABLE test.developers
(
  `dev_id`    int NOT NULL AUTO_INCREMENT,
  `dev_name`  varchar(45) DEFAULT NULL,
  PRIMARY KEY (`dev_id`)
) DEFAULT CHARSET=utf8;

INSERT INTO test.developers (`dev_name`) VALUES ('Иванов'),('Петров'),('Сидоров'),('Гадюкин');

CREATE TABLE test.projects
(
  `prj_id`    int NOT NULL AUTO_INCREMENT,
  `prj_name`  varchar(45) DEFAULT NULL,
  PRIMARY KEY (`prj_id`)
) DEFAULT CHARSET=utf8;

INSERT INTO test.projects (`prj_name`) VALUES ('Образование'),('Финансы'),('Производство'),('Исследование'),('Алкоголизм'),('Экология'),('Туризм');

CREATE TABLE test.dev_prj
(
  `dp_dev_id` int NOT NULL,
  `dp_prj_id` int NOT NULL,
  FOREIGN KEY (dp_dev_id) REFERENCES developers(dev_id),
  FOREIGN KEY (dp_prj_id) REFERENCES projects(prj_id)
) DEFAULT CHARSET=utf8;

INSERT INTO test.dev_prj (`dp_dev_id`,`dp_prj_id`) VALUES (1,1),(2,1),(3,1),(1,2),(2,2),(3,2),(4,2),(2,3),(1,4),(4,5),(1,7),(4,7);

#Список проектов, в которых нет разработчиков
SELECT prj_name FROM test.projects WHERE prj_id NOT IN(SELECT dp_prj_id FROM test.dev_prj);
SELECT b.prj_name FROM test.dev_prj AS a RIGHT JOIN test.projects AS b ON a.dp_prj_id=b.prj_id WHERE a.dp_prj_id IS NULL;

#Cписок проектов, в которых участвуют все разработчики
SELECT prj_name FROM test.projects WHERE prj_name NOT IN(SELECT b.prj_name FROM test.developers AS a JOIN test.projects AS b LEFT JOIN test.dev_prj AS c ON a.dev_id=c.dp_dev_id AND b.prj_id=c.dp_prj_id WHERE c.dp_prj_id IS NULL GROUP BY b.prj_name);
SELECT b.prj_name FROM (SELECT count(*) AS dev_cnt,dp_prj_id FROM test.dev_prj GROUP BY dp_prj_id) AS a, test.projects AS b, (SELECT COUNT(*) AS cnt FROM test.developers) AS c WHERE a.dp_prj_id=b.prj_id AND a.dev_cnt=c.cnt;

#Список проектов (с указанием количества разработчиков), в которых принимает участие четное количество разработчиков и этих разработчиков больше, чем двое.
SELECT a.dev_cnt,b.prj_name FROM (SELECT count(*) AS dev_cnt,dp_prj_id FROM test.dev_prj GROUP BY dp_prj_id) AS a, test.projects AS b WHERE a.dp_prj_id=b.prj_id AND a.dev_cnt>2 AND (a.dev_cnt % 2)=0;
