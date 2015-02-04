package de.deyovi.chat.dao;

import de.deyovi.chat.dao.entities.ProfileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileDAO extends CrudRepository<ProfileEntity, Long> {

}
