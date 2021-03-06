package com.venky.paypal.classicapi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

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

public class PayPalDeductTest {
	
	private String payPalUrl = "https://api-3t.sandbox.paypal.com/nvp";
	private String payPalUserName = "mgsbabu-facilitator_api1.gmail.com";
	private String payPalPassword = "1385957805";
	private String payPalSignature = "An5ns1Kso7MWUdW4ErQKJJJ4qi4-A-m9rs3sCPN5gTpB99BWqFKbuDJb";
	private String payPalCurrencyCode = "USD";
	private int payPalMaxFailedAttempts = 3;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Double amount = (double) 1;
		Map<String, Object> requestData = null;
		CreditCard creditCard =  new CreditCard();
		creditCard.setNumber("5520000000000000");
		  creditCard.setType(CreditCard.MASTERCARD);
		  creditCard.setCVV2("123");
		  creditCard.setFirstName("Venky");
		  creditCard.setLastName("B");
		  creditCard.setCity("BVRM");
		  creditCard.setState("AP");
		  creditCard.setZipcode("534230");
		  creditCard.setCountry("IN");
		  creditCard.setExpirationDate("052014");
		
		  new PayPalDeductTest().deductPayment(creditCard, amount, requestData);
	}
	
	public String deductPayment(CreditCard creditCard, Double amount, Map<String, Object> requestData) throws Exception {
		String transactionId = "";
		HttpPost httpMethod = new HttpPost(payPalUrl);
		
    	String paymentRequest = "USER="+ this.payPalUserName
		+"&PWD="+this.payPalPassword				//insert_merchant_password_here
		+"&SIGNATURE="+ this.payPalSignature		//insert_merchant_signature_value_here
		+"&METHOD=DoDirectPayment"
		+"&VERSION=78"
		+"&PAYMENTACTION=SALE"
		+"&AMT="+amount								// #The amount the buyer will pay in a payment period
		+"&MAXFAILEDPAYMENTS="+this.payPalMaxFailedAttempts						// #Maximum failed payments before suspension of the profile
		+"&ACCT="+creditCard.getNumber()			// #The credit card number
		+"&CREDITCARDTYPE=MASTERCARD"				// VISA/MASTERCARD
		// #The type of credit card 
		+"&CVV2="+creditCard.getCVV2()				// #The CVV2 number
		+"&FIRSTNAME="+creditCard.getFirstName()
		+"&LASTNAME="+creditCard.getLastName()
		+"&STREET="+creditCard.getAddress1()
		+"&CITY="+creditCard.getCity()
		+"&STATE="+creditCard.getState()
		+"&ZIP="+creditCard.getZipcode()
		+"&COUNTRYCODE="+creditCard.getCountry()	//#The country code, e.g. US  
		+"&CURRENCYCODE="+ this.payPalCurrencyCode	//#The currency, e.g. US dollars
		+"&EXPDATE="+creditCard.getExpirationDate();//#Expiration date of the credit card
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
