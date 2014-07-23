package com.venky.paypal.classicapi;




public class CreditCard {

	public final static int VISA = 1;
	public final static int MASTERCARD = 2;
	public final static int AMEX = 3;
	public final static int DISCOVER = 4;
	
	private String remoteIpAddr;
	
	//Credit card details
	private String number;
	private String expirationDate; //mm/yy
	private String CVV2;
	private int type;
	
	//payee details
	private String firstName;
	private String lastName;
	
	private String country;
	private String state;
	private String city;
	private String zipcode;
	private String address1;
	private String address2;
	
	public String getCVV2() {
		return CVV2;
	}
	public void setCVV2(String cvv2) {
		CVV2 = cvv2;
	}
	
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getRemoteIpAddr() {
		return remoteIpAddr;
	}
	public void setRemoteIpAddr(String remoteIpAddr) {
		this.remoteIpAddr = remoteIpAddr;
	}
	public String getNumber() {
		if(number != null)
			return number.trim();
		return number;
	}
	public void setNumber(String number) {
		if(number != null)
			this.number = number.trim();
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getNumberDisplayString() {
		String cardNumber = this.number;
		return cardNumber.replace(cardNumber.substring(0,12), "XXXXXXXXXXXX");
	}
	
}
