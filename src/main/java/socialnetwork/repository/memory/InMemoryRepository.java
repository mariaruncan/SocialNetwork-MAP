package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * repo pt memorie
 * @param <ID>
 * @param <E>
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    public Map<ID,E> entities;

    /***
     * constructor
     * @param validator
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    /***
     *
     * @return intul catitatii din lista
     */
    public int size(){return entities.size();}

    /***
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return
     */
    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    /***
     *
     * @return lista
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /***
     *
     * @param entity
     *         entity must be not null
     * @return entiatea creata
     * thrownew IllegalArgumentException
     */
    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity;
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /***
     *
     * @param id
     *      id must be not null
     * @return
     * @throws IOException
     */
    @Override
    public E delete(ID id) throws IOException {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        E entity = entities.get(id);
        if(entity==null)
            return null;
        else {
            return entities.remove(id);
        }
    }

    /***
     *
     * @param entity
     *          entity must not be null
     * @return
     */
    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

}
