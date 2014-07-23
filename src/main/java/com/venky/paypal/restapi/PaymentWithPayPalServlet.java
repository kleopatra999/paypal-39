package com.venky.paypal.restapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.AmountDetails;
import com.paypal.api.payments.Link;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.core.rest.PayPalResource;

public class PaymentWithPayPalServlet {

	Map<String, String> map = new HashMap<String, String>();
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new PaymentWithPayPalServlet().init();
		new PaymentWithPayPalServlet().doPost(null, null);
		//guid=&token=EC-4AG86384UF849094T&PayerID=
		//new PaymentWithPayPalServlet().doPost("NMLGYE6S7F5S8", "d733146b4be04401b51fac1030d68515");
	}
	
	public void init() throws Exception {
		// ##Load Configuration
		// Load SDK configuration for
		// the resource. This intialization code can be
		// done as Init Servlet.
		InputStream is = PaymentWithPayPalServlet.class.getResourceAsStream("sdk_config.properties");
		try {
			PayPalResource.initConfig(is);
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}

	}
	
	protected void doPost(String requestPayerId, String requestGuid) throws IOException {
		// ###AccessToken
		// Retrieve the access token from
		// OAuthTokenCredential by passing in
		// ClientID and ClientSecret
		com.paypal.core.rest.APIContext apiContext = null;
		String accessToken = null;
		try {
			accessToken = GenerateAccessToken.getAccessToken();

			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			apiContext = new APIContext(accessToken);
			// Use this variant if you want to pass in a request id
			// that is meaningful in your application, ideally
			// a order id.
			/*
			 * String requestId = Long.toString(System.nanoTime(); APIContext
			 * apiContext = new APIContext(accessToken, requestId ));
			 */
		} catch (PayPalRESTException e) {
			System.out.println("error:" + e.getMessage());
			//req.setAttribute("error", e.getMessage());
		}
		if (requestPayerId != null) {
			Payment payment = new Payment();
			if (requestGuid != null) {
				payment.setId("PAY-1B310667PW134574JKMECNPA");
			}

			PaymentExecution paymentExecution = new PaymentExecution();
			paymentExecution.setPayerId(requestPayerId);
			try {
				Payment createdPayment = payment.execute(apiContext, paymentExecution);
				System.out.println("response" + Payment.getLastResponse());
				System.out.println("PaymentId:" + createdPayment.getId());
				//req.setAttribute("response", Payment.getLastResponse());
			} catch (PayPalRESTException e) {
				System.out.println("error:" + e.getMessage());
				//req.setAttribute("error", e.getMessage());
			}
		} else {

			// ###Details
			// Let's you specify details of a payment amount.
			AmountDetails details = new AmountDetails();
			details.setShipping("1");
			details.setSubtotal("5");
			details.setTax("1");

			// ###Amount
			// Let's you specify a payment amount.
			Amount amount = new Amount();
			amount.setCurrency("USD");
			// Total must be equal to sum of shipping, tax and subtotal.
			amount.setTotal("7");
			amount.setDetails(details);

			// ###Transaction
			// A transaction defines the contract of a
			// payment - what is the payment for and who
			// is fulfilling it. Transaction is created with
			// a `Payee` and `Amount` types
			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			transaction.setDescription("This is the payment transaction description.");

			// The Payment creation API requires a list of
			// Transaction; add the created `Transaction`
			// to a List
			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

			// ###Payer
			// A resource representing a Payer that funds a payment
			// Payment Method
			// as 'paypal'
			Payer payer = new Payer();
			payer.setPaymentMethod("paypal");

			// ###Payment
			// A Payment Resource; create one using
			// the above types and intent as 'sale'
			Payment payment = new Payment();
			payment.setIntent("sale");
			payment.setPayer(payer);
			payment.setTransactions(transactions);

			// ###Redirect URLs
			RedirectUrls redirectUrls = new RedirectUrls();
			String guid = UUID.randomUUID().toString().replaceAll("-", "");
			redirectUrls.setCancelUrl("https://www.google.co.in?cancelUrl=true&guid=" + guid);
			redirectUrls.setReturnUrl("https://www.google.co.in?returnUrl=true&guid=" + guid);
			payment.setRedirectUrls(redirectUrls);

			// Create a payment by posting to the APIService
			// using a valid AccessToken
			// The return object contains the status;
			try {
				Payment createdPayment = payment.create(apiContext);
				System.out.println("Created payment with id = "
						+ createdPayment.getId() + " and status = "
						+ createdPayment.getState());
				// ###Payment Approval Url
				Iterator<Link> links = createdPayment.getLinks().iterator();
				while (links.hasNext()) {
					Link link = links.next();
					if (link.getRel().equalsIgnoreCase("approval_url")) {
						//req.setAttribute("redirectURL", link.getHref());
						System.out.println("redirectURL:"+ link.getHref());
					}
				}
				System.out.println("response:"+ Payment.getLastResponse());
				//req.setAttribute("response", Payment.getLastResponse());
				map.put(guid, createdPayment.getId());
				System.out.println("guid:" + guid);
				System.out.println("created paymentId:" + createdPayment.getId());
			} catch (PayPalRESTException e) {
				System.out.println("error:" + e.getMessage());
				//req.setAttribute("error", e.getMessage());
			}
		}
		//req.setAttribute("request", Payment.getLastRequest());
		//req.getRequestDispatcher("response.jsp").forward(req, resp);
		System.out.println("request:" + Payment.getLastRequest());
		System.out.println("response.jsp");
	}

}
