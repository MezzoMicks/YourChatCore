package de.deyovi.chat.dao;

import de.deyovi.chat.dao.entities.ImageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDAO extends CrudRepository<ImageEntity, Long> {

}
