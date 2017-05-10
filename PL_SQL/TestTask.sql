CREATE OR REPLACE PROCEDURE TESTTASK(inVar IN VARCHAR2)
IS
  tmpStr VARCHAR2(250);
  fromPos NUMBER;
  toPos NUMBER;
BEGIN
  toPos := 0;
  WHILE (toPos < LENGTH(inVar))
  LOOP
    fromPos := toPos+1;
    toPos := INSTR(inVar,';',fromPos);
    IF (toPos = 0)
    THEN
      toPos := LENGTH(inVar)+1;
    END IF;
    tmpStr := SUBSTR(inVar,fromPos,toPos-fromPos);
    tmpStr := REGEXP_REPLACE(tmpStr,'^ ','');
    IF (REGEXP_LIKE(tmpStr,'^\d{1,10}([\.\,]\d{1,10}){0,1}$'))
    THEN
      DBMS_OUTPUT.put_line(CONCAT('Number: ',tmpStr));
    ELSE
      DBMS_OUTPUT.put_line(CONCAT('String: ',tmpStr));
    END IF;
  END LOOP;
END;

CALL TESTTASK('10.3; asd123; a33; 0,1');
CALL TESTTASK('0123456789.3; asd.123; a3 3; 01234567890,1');
CALL TESTTASK(';10.3; asd123; a33; 0,1;');