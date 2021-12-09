package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

/**
 * validator class for a friendship
 */
public class FriendshipValidator implements  Validator<Friendship> {
    @Override
    /**
     * validates a friendship
     * @param entity - Friendship
     * @throw ValidationException
     */
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getUser1() == entity.getUser2())
            throw new ValidationException("An user can not be friend with himself!");
    }
}
