package com.venky.paypal.restapi;

import java.io.IOException;
import java.io.InputStream;

import com.paypal.api.payments.Payment;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.core.rest.PayPalResource;

/**
* @author venky
* 
*/
public class GetPayment {
	
	public static void main(String[] args) throws Exception {
		new GetPayment().init();
		new GetPayment().doPost();
	}

	public void init() throws Exception {
		// ##Load Configuration
		// Load SDK configuration for
		// the resource. This intialization code can be
		// done as Init Servlet.
		InputStream is = GetPayment.class.getResourceAsStream("sdk_config.properties");
		try {
			PayPalResource.initConfig(is);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}

	}

	protected void doPost() throws IOException {
		String paymentId = "PAY-4YV15391L5287131KKMM2HQA";
		Payment payment = null;
		try {
			String accessToken = GenerateAccessToken.getAccessToken();

			payment = Payment.get(accessToken, paymentId);
			
			System.out.println("Getting payment details with id = " + payment.getId());
			System.out.println(payment.getLastResponse());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
