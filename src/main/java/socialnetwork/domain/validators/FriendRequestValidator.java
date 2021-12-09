package socialnetwork.domain.validators;

import socialnetwork.domain.FriendRequest;

public class FriendRequestValidator implements Validator<FriendRequest> {
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if(!(entity.getStatus().matches("^approved$") || entity.getStatus().matches("^pending$") ||
                entity.getStatus().matches("^rejected$")))
            throw new ValidationException("Invalid status!");
        if(entity.getFrom().getId() == entity.getTo().getId())
            throw new ValidationException("An user can not send a friend request to himself!");
    }
}
