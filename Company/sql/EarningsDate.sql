SELECT EarningsDate.*, Company.analystOpinion, Company.name, Company.jpmOpinion
FROM EarningsDate, Company 
WHERE EarningsDate.companyId = Company.id AND EarningsDate.earningsDate = '2017-01-26' AND Company.analystOpinion IS NOT NULL
ORDER BY Company.jpmOpinion ASC, Company.analystOpinion ASC;