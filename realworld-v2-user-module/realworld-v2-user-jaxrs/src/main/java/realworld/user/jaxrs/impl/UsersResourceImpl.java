package realworld.user.jaxrs.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import realworld.user.jaxrs.UsersResource;
import realworld.user.model.UserData;
import realworld.user.model.UserRegistrationData;
import realworld.user.model.UserUpdateData;
import realworld.user.services.UserService;

/**
 * Implementation of the {@link UsersResource}.
 */
@RequestScoped
public class UsersResourceImpl implements UsersResource {

	@Inject
	private UserService userService;

	@Context
	private UriInfo uriInfo;

	@Override
	public Response register(UserRegistrationData registerParam) {
		UserData u = userService.register(registerParam);
		return Response.created(uriInfo.getRequestUriBuilder().path(UsersResource.class, "get").build(u.getUsername())).build();
	}

	@Override
	public UserData get(String username) {
		return userService.findByUserName(username);
	}

	@Override
	public Response update(UserUpdateData updateParam) {
		userService.update(updateParam);
		return Response.noContent().build();
	}
}
