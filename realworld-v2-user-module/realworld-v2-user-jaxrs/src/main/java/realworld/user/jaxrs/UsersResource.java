package realworld.user.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import realworld.user.model.UserRegistrationData;

/**
 * User operations.
 */
@Path("/users")
@Api(tags= UsersResource.TAG)
public interface UsersResource {

	String TAG = "UsersResource";

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Register user.", tags=TAG)
	Response register(
			@ApiParam(value = "Information required to register.", required = true)
			UserRegistrationData registerParam
	);

	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value="Returns the profile of the given user.", tags=TAG)
	Response get(
			@ApiParam(value = "The user name to apply this operation to.", required = true)
			@PathParam("username")
			String username
	);
}
