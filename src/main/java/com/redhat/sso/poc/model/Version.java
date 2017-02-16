package com.redhat.sso.poc.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Version {
	private String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
