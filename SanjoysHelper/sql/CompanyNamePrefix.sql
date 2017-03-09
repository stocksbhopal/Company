insert into Company (symbol, name, ipoyear, industry, sector, exchange, marketcap, marketcapbm)
select symbol, name, ipoyear, industry, sector, exchange, marketcap, marketcapbm from CompanyStage where symbol not in (select symbol from Company);
commit;

insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('HAL', 'haliburton', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('NOW', 'service now', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('X', 'us steel', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('UA', 'under armor', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('PLCE', "children's", 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('BKU', 'bank united', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('EPD', 'enterprise lp', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('CRM', 'sales force', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('CHKP', 'checkpoint', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('PCLN', 'price line', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('GDDY', 'go daddy', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('GDDY', 'gold daddy', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('SNPS', 'synopsis', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('T', 'at and t', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('T', 'tea', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('FL', 'footlocker', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('JNJ', 'johnson and johnson', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('L', 'lose', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('L', 'loaves', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('AMZN', 'amazon', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('GOOG', 'google', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('HON', 'honey well', 1);

update CompanyNamePrefix set symbol = 'SNAP' where companyNamePrefix = 'snap';

