package realworld.user.model;

import org.immutables.value.Value;

/**
 * User profile full data.
 */
@Value.Immutable
public interface UserData {

	/** Get the user id. */
	String getId();

	/** Get the user name. */
	String getUsername();

	/** Get the user email. */
	String getEmail();

	/** Get the link to the user image. */
	@Nullable
	String getImageUrl();
}
