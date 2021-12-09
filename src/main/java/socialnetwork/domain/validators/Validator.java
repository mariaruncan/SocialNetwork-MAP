package socialnetwork.domain.validators;

/***
 * validator interface
 * @param <T>
 */
public interface Validator<T> {
    /***
     * validation function
     * @param entity
     * @throws ValidationException
     */
    void validate(T entity) throws ValidationException;
}