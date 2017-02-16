package com.redhat.sso.poc.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.redhat.sso.poc.model.Version;

@WebService
public interface VersionWS {
	@WebMethod
	public Version getVersion();
}
