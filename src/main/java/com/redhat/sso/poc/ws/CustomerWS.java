package com.redhat.sso.poc.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.redhat.sso.poc.model.Pessoa;

@WebService(serviceName="CustomerWS")
public interface CustomerWS {
	@WebMethod
	public Pessoa create(Pessoa pessoa);
	
	@WebMethod
	public List<Pessoa> list();
}
