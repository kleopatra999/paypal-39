package com.venky.paypal.classicapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetPayPalPaymentDetails {

	private String payPalUrl = "https://api-3t.sandbox.paypal.com/nvp";
	private String payPalUserName = "mgsbabu-facilitator_api1.gmail.com";
	private String payPalPassword = "1385957805";
	private String payPalSignature = "An5ns1Kso7MWUdW4ErQKJJJ4qi4-A-m9rs3sCPN5gTpB99BWqFKbuDJb";
	//private String payKey = "AP-3TY011106S4428730";
	private String transactionId = "0C3506878G627961D";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new GetPayPalPaymentDetails().getPaymentDetails();
	}

	public String getPaymentDetails() throws Exception {
		HttpPost httpMethod = new HttpPost(payPalUrl);
		
    	String paymentRequest = "USER="+ this.payPalUserName
		+"&PWD="+this.payPalPassword				//insert_merchant_password_here
		+"&SIGNATURE="+ this.payPalSignature		//insert_merchant_signature_value_here
		+"&METHOD=GetTransactionDetails"
		+"&TRANSACTIONID="+transactionId
		//+"&PAYID="+payKey
		+"&VERSION=94";
		
		try {
	    	StringEntity entity = new StringEntity(paymentRequest.toString(), "UTF-8");
	        entity.setContentType("application/json");
			httpMethod.setEntity(entity);
			handleHttpMethod(httpMethod);
			return transactionId;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to execute the payment transaction, Reason:"+ ex.toString());
		}
	}
	
	private static void handleHttpMethod(HttpRequestBase httpMethod) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
        	httpclient = (DefaultHttpClient) wrapClient(httpclient);
        	HttpResponse httpResponse = httpclient.execute(httpMethod);

        	int statusCode = httpResponse.getStatusLine().getStatusCode();
			System.out.println("Status Code: "+statusCode);
			InputStream in = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			String reqStr = sb.toString();
			System.out.println("Response Str : " + reqStr);
			if(reqStr != null) {
				String[] outputParameters = reqStr.split("&");
				for(String parameter: outputParameters) {
					String key = URLDecoder.decode(parameter, "UTF-8");
					System.out.println(key);
				}
			}
			if(statusCode != HttpStatus.SC_OK) {
				String resData = "";
				Header[] headers = httpResponse.getAllHeaders();
	            for(int j=0; j<headers.length; j++) {
	            	if(headers[j].getName().equalsIgnoreCase("ERROR")) {
	            		resData = headers[j].getValue();
	            	}
	            }
	            System.out.println("ERROR: " + resData);
			}

        } finally {
            // release any connection resources used by the method
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
	
	@SuppressWarnings("deprecation")
	private static HttpClient wrapClient(HttpClient base) {
    	try {
	    	SSLContext ctx = SSLContext.getInstance("TLS");
	    	
	    	X509TrustManager tm = new X509TrustManager() {

		    	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		    	}
	
		    	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		    	}
	
		    	public X509Certificate[] getAcceptedIssuers() {
		    		return null;
		    	}
	    	};
	    	
	    	ctx.init(null, new TrustManager[]{tm}, null);
	    	SSLSocketFactory ssf = new SSLSocketFactory(ctx);
	    	ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	    	ClientConnectionManager ccm = base.getConnectionManager();
	    	SchemeRegistry sr = ccm.getSchemeRegistry();
	    	sr.register(new Scheme("https", ssf, 443));
	    	return new DefaultHttpClient(ccm, base.getParams());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    }
}
