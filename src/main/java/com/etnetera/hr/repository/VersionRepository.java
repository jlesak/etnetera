package com.etnetera.hr.repository;

import com.etnetera.hr.data.Version;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring data repository interface used for accessing the data in database.
 * 
 * @author Etnetera
 *
 */
public interface VersionRepository extends CrudRepository<Version, Long> {

}
