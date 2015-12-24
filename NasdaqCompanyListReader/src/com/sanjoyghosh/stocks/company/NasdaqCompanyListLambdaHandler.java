package com.sanjoyghosh.stocks.company;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.S3Object;
import com.sanjoyghosh.company.nasdaqcompanylist.NasdaqCompanyListReader;

public class NasdaqCompanyListLambdaHandler implements RequestHandler<S3Event, Object> {

    @Override
    public Object handleRequest(S3Event s3event, Context context) {    
        S3EventNotificationRecord s3Record = s3event.getRecords().get(0);
        String s3Bucket = s3Record.getS3().getBucket().getName();
        String s3Key = s3Record.getS3().getObject().getKey();
        System.out.println("S3: " + s3Bucket + "  " + s3Key);
        
        AmazonS3Client s3Client = new AmazonS3Client();
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
    }

}
