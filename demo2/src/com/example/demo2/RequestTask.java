package com.example.demo2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

public class RequestTask extends AsyncTask<Void,Void,Void> {
	public String xCoord;
	public String yCoord;
	
    public String getxCoord() {
		return xCoord;
	}
	public void setxCoord(String xCoord) {
		this.xCoord = xCoord;
	}
	public String getyCoord() {
		return yCoord;
	}
	public void setyCoord(String yCoord) {
		this.yCoord = yCoord;
	}
	@Override
    protected Void doInBackground(Void... params) {

        String url = "http://heat-map.mybluemix.net/collectData";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try{
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(2);
            //Adding name-value pairs as arguments for the post request
            nameValuePairList.add(new BasicNameValuePair("x", xCoord));
            nameValuePairList.add(new BasicNameValuePair("y", yCoord));
            //Sets parameters for the post request
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));

            //Receive a response indicating a message for failure or success
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String output = inputStreamToString(httpResponse.getEntity().getContent()).toString();
            System.out.println(output);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
	//Function that reads in stuff from the stream and creates a built string
    public StringBuilder inputStreamToString(InputStream is){
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }
}