package com.redhat.sso.poc.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.redhat.sso.poc.model.Pessoa;
import com.redhat.sso.poc.model.PessoaList;

@WebService(serviceName="CustomerWS")
public interface CustomerWS {
	@WebMethod
	public Pessoa create(Pessoa pessoa);
	
	@WebMethod
	public PessoaList list();
}
