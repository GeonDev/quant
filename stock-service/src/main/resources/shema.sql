CREATE TABLE IF NOT EXISTS tb_corp_code (
  corp_code varchar(11) NOT NULL,
  corp_name text,
  stock_code varchar(11) ,
  state varchar(5) DEFAULT 'Y' COMMENT 'Y : 정상 , N : 거래정지 또는 상장 폐지',
  option varchar(11) ,
  message text ,
  check_dt date,
  PRIMARY KEY (corp_code)
);


CREATE TABLE IF NOT EXISTS tb_corp_finance (
  rcept_no varchar(11) NOT NULL,
  corp_code varchar(11) ,
  stock_code varchar(11) ,
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
  PRIMARY KEY (rcept_no)
);


CREATE TABLE IF NOT EXISTS tb_stock_price (
  price_id BIGINT NOT NULL,
  stock_code varchar(11) ,
  market_code varchar(11) ,
  bas_dt date ,
  volume int ,
  start_price int ,
  end_price int ,
  high_price int ,
  low_price int ,
  daily_range decimal(11,0) ,
  daily_ratio decimal(11,2) ,
  stock_total_cnt bigint ,
  market_total_amt bigint ,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_stock_price_average (
      stock_code varchar(11) NOT NULL,
      tar_dt date ,
      price_type varchar(11) ,
      price int ,
      PRIMARY KEY (stock_code)
);