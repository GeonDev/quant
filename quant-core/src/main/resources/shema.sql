-- Autogenerated: do not edit this file

drop all objects;

CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS TB_CORP_INFO (
  corp_code varchar(11) NOT NULL,
  corp_name text,
  funding bigint,
  stock_code varchar(11) ,
  income varchar(10) ,
  state varchar(10) DEFAULT 'ACTIVE',
  corp_type varchar(11) ,
  message text ,
  check_dt date,
  momentum int,
  PRIMARY KEY (corp_code)
);

CREATE INDEX tci_idx01 ON TB_CORP_INFO (state, corp_type);


CREATE TABLE IF NOT EXISTS TB_CORP_FINANCE (
  rcept_no varchar(20) NOT NULL,
  reprt_code varchar(20),
  corp_code varchar(11) ,
  stock_code varchar(11) ,
  year_code varchar(4),
  currency varchar(8),
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
  PRIMARY KEY (rcept_no)
);

CREATE INDEX tcf_idx01 ON TB_CORP_FINANCE (corp_code, reprt_code, year_code);

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
  PRIMARY KEY (price_id)
);

CREATE INDEX tsp_idx01 ON TB_STOCK_PRICE (stock_code, bas_dt);


CREATE TABLE IF NOT EXISTS TB_STOCK_AVERAGE (
  stock_code varchar(11) NOT NULL,
  tar_dt date ,
  price_type varchar(11) ,
  price int ,
  PRIMARY KEY (stock_code)
);

CREATE INDEX tsa_idx01 ON TB_STOCK_AVERAGE (stock_code, tar_dt);

CREATE TABLE IF NOT EXISTS TB_STOCK_TRADE_HISTORY (
  trade_id bigint NOT NULL AUTO_INCREMENT,
  user_key varchar(12) ,
  stock_code varchar(11) ,
  trading_Dt date ,
  price int ,
  stock_count int ,
  trade_type varchar(11) ,
  PRIMARY KEY (trade_id)
);

CREATE TABLE IF NOT EXISTS TB_STOCK_PORTFOLIO (
  portfolio_id varchar(12) NOT NULL,
  total_value bigint,
  user_key varchar(12) ,
  momentum_score int ,
  stock_count int ,
  market varchar(10) ,
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
