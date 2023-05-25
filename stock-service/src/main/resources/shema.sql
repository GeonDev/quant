CREATE TABLE tb_corp_code (
  corp_code varchar(11) NOT NULL,
  corp_name text,
  stock_code varchar(11) DEFAULT NULL,
  corp_state varchar(5) DEFAULT 'Y' COMMENT 'Y : 정상 , N : 거래정지 또는 상장 폐지',
  PRIMARY KEY (corp_code)
)


CREATE TABLE tb_corp_finance (
  rcept_no varchar(11) NOT NULL,
  corp_code varchar(11) DEFAULT NULL,
  stock_code varchar(11) DEFAULT NULL,
  start_dt date DEFAULT NULL,
  end_dt date DEFAULT NULL,
  capital bigint(20) DEFAULT NULL,
  total_assets bigint(20) DEFAULT NULL,
  total_debt bigint(20) DEFAULT NULL,
  total_equity bigint(20) DEFAULT NULL,
  revenue bigint(20) DEFAULT NULL,
  operating_profit bigint(20) DEFAULT NULL,
  net_income bigint(20) DEFAULT NULL,
  earned_surplus bigint(20) DEFAULT NULL,
  PRIMARY KEY (rcept_no)
)


CREATE TABLE tb_stock_price (
  stock_code varchar(11) NOT NULL,
  market_code varchar(11) DEFAULT NULL,
  bas_dt date DEFAULT NULL,
  volume int(11) DEFAULT NULL,
  start_price int(11) DEFAULT NULL,
  end_price int(11) DEFAULT NULL,
  high_price int(11) DEFAULT NULL,
  low_price int(11) DEFAULT NULL,
  daily_range decimal(11,0) DEFAULT NULL,
  daily_ratio decimal(11,2) DEFAULT NULL,
  stock_total_cnt bigint(11) DEFAULT NULL,
  market_total_amt bigint(11) DEFAULT NULL,
  PRIMARY KEY (stock_code)
)

