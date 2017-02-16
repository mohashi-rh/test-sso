package com.redhat.sso.poc.rs;

import static javax.ws.rs.core.MediaType.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.redhat.sso.poc.model.Version;

@Path("/version")
public interface VersionRS {
	@GET
	@Produces(APPLICATION_JSON)
	public Version getVersion();
}
