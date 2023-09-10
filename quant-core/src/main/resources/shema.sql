-- Autogenerated: do not edit this file

CREATE TABLE IF NOT EXISTS BATCH_JOB_INSTANCE  (
                                     JOB_INSTANCE_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
                                     VERSION BIGINT ,
                                     JOB_NAME VARCHAR(100) NOT NULL,
                                     JOB_KEY VARCHAR(32) NOT NULL,
                                     constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION  (
                                      JOB_EXECUTION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
                                      VERSION BIGINT  ,
                                      JOB_INSTANCE_ID BIGINT NOT NULL,
                                      CREATE_TIME TIMESTAMP(9) NOT NULL,
                                      START_TIME TIMESTAMP(9) DEFAULT NULL ,
                                      END_TIME TIMESTAMP(9) DEFAULT NULL ,
                                      STATUS VARCHAR(10) ,
                                      EXIT_CODE VARCHAR(2500) ,
                                      EXIT_MESSAGE VARCHAR(2500) ,
                                      LAST_UPDATED TIMESTAMP(9),
                                      constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
                                          references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_PARAMS  (
                                             JOB_EXECUTION_ID BIGINT NOT NULL ,
                                             PARAMETER_NAME VARCHAR(100) NOT NULL ,
                                             PARAMETER_TYPE VARCHAR(100) NOT NULL ,
                                             PARAMETER_VALUE VARCHAR(2500) ,
                                             IDENTIFYING CHAR(1) NOT NULL ,
                                             constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
                                                 references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION  (
                                       STEP_EXECUTION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
                                       VERSION BIGINT NOT NULL,
                                       STEP_NAME VARCHAR(100) NOT NULL,
                                       JOB_EXECUTION_ID BIGINT NOT NULL,
                                       CREATE_TIME TIMESTAMP(9) NOT NULL,
                                       START_TIME TIMESTAMP(9) DEFAULT NULL ,
                                       END_TIME TIMESTAMP(9) DEFAULT NULL ,
                                       STATUS VARCHAR(10) ,
                                       COMMIT_COUNT BIGINT ,
                                       READ_COUNT BIGINT ,
                                       FILTER_COUNT BIGINT ,
                                       WRITE_COUNT BIGINT ,
                                       READ_SKIP_COUNT BIGINT ,
                                       WRITE_SKIP_COUNT BIGINT ,
                                       PROCESS_SKIP_COUNT BIGINT ,
                                       ROLLBACK_COUNT BIGINT ,
                                       EXIT_CODE VARCHAR(2500) ,
                                       EXIT_MESSAGE VARCHAR(2500) ,
                                       LAST_UPDATED TIMESTAMP(9),
                                       constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
                                           references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT  (
                                               STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                               SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                               SERIALIZED_CONTEXT LONGVARCHAR ,
                                               constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
                                                   references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT  (
                                              JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                              SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                              SERIALIZED_CONTEXT LONGVARCHAR ,
                                              constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
                                                  references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE SEQUENCE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_SEQ;



CREATE TABLE IF NOT EXISTS TB_CORP_INFO (
  corp_code varchar(11) NOT NULL,
  corp_name text,
  stock_code varchar(11) ,
  income varchar(10) ,
  state varchar(10) DEFAULT 'ACTIVE',
  corp_type varchar(11) ,
  message text ,
  check_dt date,
  PRIMARY KEY (corp_code)
);


CREATE TABLE IF NOT EXISTS TB_CORP_FINANCE (
  finance_id bigint NOT NULL AUTO_INCREMENT,
  rcept_no varchar(20),
  corp_code varchar(11) ,
  stock_code varchar(11) ,
  year_code varchar(4),
  start_dt date ,
  end_dt date ,
  capital bigint,
  total_assets bigint,
  total_debt bigint,
  total_equity bigint,
  revenue bigint,
  operating_profit bigint,
  net_income bigint,
  earned_surplus bigint,
  PSR double DEFAULT 0,
  PBR double DEFAULT 0,
  PER double DEFAULT 0,
  POR double DEFAULT 0,
  YOY double DEFAULT 0,
  QOQ double DEFAULT 0,
  OPGE double DEFAULT 0,
  PGE double DEFAULT 0,
  PRIMARY KEY (finance_id)
);


CREATE TABLE IF NOT EXISTS TB_STOCK_PRICE (
  price_id bigint NOT NULL AUTO_INCREMENT,
  stock_code varchar(11) ,
  market_code varchar(11) ,
  bas_dt date ,
  volume int ,
  start_price int ,
  end_price int ,
  high_price int ,
  low_price int ,
  daily_range double ,
  daily_ratio double ,
  stock_total_cnt bigint ,
  market_total_amt bigint ,
  momentum int,
  PRIMARY KEY (price_id)
);


CREATE TABLE IF NOT EXISTS TB_STOCK_AVERAGE (
  stock_code varchar(11) NOT NULL,
  tar_dt date ,
  price_type varchar(11) ,
  price int ,
  PRIMARY KEY (stock_code)
);


CREATE TABLE IF NOT EXISTS TB_STOCK_TRADE (
  trade_id bigint NOT NULL AUTO_INCREMENT,
  user_key varchar(12) ,
  trading_type varchar(11) ,
  trading_Dt date ,
  average int ,
  stock_count int ,
  PRIMARY KEY (trade_id)
);

CREATE TABLE IF NOT EXISTS TB_STOCK_PORTFOLIO (
  portfolio_id varchar(12) NOT NULL,
  user_key varchar(12) ,
  momentum_score int ,
  stock_count int ,
  ranges varchar(20) ,
  indicator varchar(512),
  rebalance varchar(256),
  comment varchar(512),
  PRIMARY KEY (portfolio_id)
);

CREATE TABLE IF NOT EXISTS TB_USER_INFO (
    user_key varchar(12) NOT NULL,
    email varchar(30),
    PRIMARY KEY (user_key)
)
