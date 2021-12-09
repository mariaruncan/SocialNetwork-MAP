package socialnetwork.domain.validators;

import socialnetwork.domain.User;

/***
 * validator class for an user
 */
public class UserValidator implements Validator<User> {
    @Override
    /***
     * validate function for a user
     * @param entity - User
     * @throw ValidationException
     */
    public void validate(User entity) throws ValidationException {
        if(entity.getFirstName().isEmpty() || entity.getLastName().isEmpty())
            throw new ValidationException("Invalid user!");
    }
}
