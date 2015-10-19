package com.sanjoyghosh.stocks.company;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.sanjoyghosh.stocks.library.db.JPAHelper;

public class NasdaqCompanyListLambdaHandler implements RequestHandler<S3Event, Object> {

    @Override
    public Object handleRequest(S3Event s3event, Context context) {
        context.getLogger().log("Input: " + "ENTER THE DRAGON AGAIN");
    	JPAHelper.createEntityManager();
    	return null;
    	
    	/*
        S3EventNotificationRecord s3Record = s3event.getRecords().get(0);
        String s3Bucket = s3Record.getS3().getBucket().getName();
        String s3Key = s3Record.getS3().getObject().getKey();
        
        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAJILCPR67C4ZL4QDA", "y9KJGkXetAyvzpF9+J7rHIePQNLGiWT/NoH4yVa5"));
        S3Object s3Object = s3Client.getObject(s3Bucket, s3Key);
        Reader s3Reader = null;
        try {
        	s3Reader = new InputStreamReader(s3Object.getObjectContent(), "UTF-8");
        	NasdaqCompanyListReader companyListReader = new NasdaqCompanyListReader();
        	companyListReader.readCompanyList(s3Reader);
        } 
        catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
        finally {
        	if (s3Reader != null) {
        		try {
        			s3Reader.close();
				} 
        		catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }

        context.getLogger().log("Input: " + s3Bucket + "  " + s3Key);
        // TODO: implement your handler
        return null;
        */
    }

}
