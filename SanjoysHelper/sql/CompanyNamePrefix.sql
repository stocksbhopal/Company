insert into Company (symbol, name, ipoyear, industry, sector, exchange, marketcap, marketcapbm)
select symbol, name, ipoyear, industry, sector, exchange, marketcap, marketcapbm from CompanyStage where symbol not in (select symbol from Company);
commit;

insert into Company (symbol, name, speechname, industry, sector, exchange) values ('DJIA', 'Dow Jones Industrial Average', 'the dow jones industrial average', 'n/a', 'n/a', 'n/a');
insert into Company (symbol, name, speechname, industry, sector, exchange) values ('IXIC', 'NASDAQ Composite', 'nasdaq composite', 'n/a', 'n/a', 'n/a');
insert into Company (symbol, name, speechname, industry, sector, exchange) values ('GSPC', 'S&P 500', 's and p 500', 'n/a', 'n/a', 'n/a');


insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('DJIA', 'the dow', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('DJIA', 'the dow jones', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('DJIA', 'the dow jones industrial average', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('DJIA', 'dow jones', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('DJIA', 'dow jones industrial average', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('IXIC', 'the nasdaq', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('IXIC', 'the nasdaq composite', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('IXIC', 'nasdaq', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('IXIC', 'nasdaq composite', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('GSPC', 'the s and p', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('GSPC', 'the s and p 500', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('GSPC', 's and p', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('GSPC', 's and p 500', 1);
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
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('SWKS', 'sky works', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('MBLY', 'mobile i', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('C', 'city group', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('C', 'citi bank', 1);


update CompanyNamePrefix set symbol = 'SNAP' where companyNamePrefix = 'snap';
update CompanyNamePrefix set companyNamePrefix = 'CA' where symbol = 'CA';
update CompanyNamePrefix set companyNamePrefix = 'HP' where symbol = 'HPQ';
update CompanyNamePrefix set companyNamePrefix = 'KT' where symbol = 'KT';
update CompanyNamePrefix set companyNamePrefix = 'The9' where symbol = 'NCTY';
delete from CompanyNamePrefix where symbol = 'OIBR';
update CompanyNamePrefix set companyNamePrefix = 'YY' where symbol = 'YY';
update CompanyNamePrefix set companyNamePrefix = 'CF' where symbol = 'CFCO';
update CompanyNamePrefix set companyNamePrefix = 'CF' where symbol = 'CFCOU';
update CompanyNamePrefix set companyNamePrefix = 'CF' where symbol = 'CFCOW';
update CompanyNamePrefix set companyNamePrefix = 'BP' where symbol = 'BP';


insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('SNAP', 'snapchat', 1);
insert into CompanyNamePrefix (symbol, companyNamePrefix, manuallyAdded) values ('SNAP', 'snap chat', 1);

// Take out the useless Comcast Companies.
delete from CompanyNamePrefix where symbol = 'CCV';
delete from CompanyNamePrefix where symbol = 'CCZ';
delete from CompanyNamePrefix where symbol = 'TLLP' and companyNamePrefix = 'tesoro';

delete FROM Company.CompanyNamePrefix where symbol = 'EOD';
delete FROM Company.Company where symbol = 'EOD';
delete from Company.CompanyNamePrefix where symbol in (select symbol from Company.Company where name like '%Fund');
delete from Company.Company where name like '%Fund';
delete from Company.CompanyNamePrefix where symbol in (select symbol from Company.Company where name like '% fund' or name like '% fund %');
delete from Company.Company where name like '% fund' or name like '% fund %';
delete from Company.CompanyNamePrefix where symbol in (select symbol from Company.Company where name like '% Fund' or name like '% Fund, %');
delete from Company.Company where name like '% Fund' or name like '% Fund, %';

