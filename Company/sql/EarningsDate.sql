SELECT EarningsDate.*, Company.name  FROM EarningsDate, Company where EarningsDate.companyId = Company.id and EarningsDate.earningsDate = '2016-05-25' order by analystOpinion asc;