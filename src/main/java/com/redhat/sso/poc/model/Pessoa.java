package com.redhat.sso.poc.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Pessoa {
	private String name;
	private String email;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public boolean equals(Object obj) {
		if ( obj != null && (obj instanceof Pessoa) ) {
			Pessoa p = (Pessoa) obj;
			
			return ((this.getName() == p.getName() || this.getName().equals(p.getName()))) &&
				   ((this.getEmail() == p.getEmail() || this.getEmail().equals(p.getEmail())));
		}
		
		return false;
	}
	@Override
	public String toString() {
		return "Pessoa [name="+this.name+",email="+this.email+"]";
	}
}
