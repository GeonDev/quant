CREATE TABLE IF NOT EXISTS tb_corp_info (
  corp_code varchar(11) NOT NULL,
  corp_name text,
  stock_code varchar(11) ,
  state varchar(5) DEFAULT 'Y' COMMENT 'Y : 정상 , N : 거래정지 또는 상장 폐지',
  corp_type varchar(11) ,
  message text ,
  check_dt date,
  PRIMARY KEY (corp_code)
);


CREATE TABLE IF NOT EXISTS tb_corp_finance (
  finance_id BIGINT NOT NULL,
  rcept_no varchar(5),
  corp_code varchar(11) ,
  stock_code varchar(11) ,
  years varchar(4),
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
  PSR double,
  PBR double,
  PER double,
  PRIMARY KEY (finance_id)
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
  daily_range double ,
  daily_ratio double ,
  stock_total_cnt bigint ,
  market_total_amt bigint ,
  PRIMARY KEY (price_id)
);

CREATE TABLE IF NOT EXISTS tb_stock_price_average (
      stock_code varchar(11) NOT NULL,
      tar_dt date ,
      price_type varchar(11) ,
      price int ,
      PRIMARY KEY (stock_code)
);