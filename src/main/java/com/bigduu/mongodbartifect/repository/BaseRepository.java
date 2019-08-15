package com.bigduu.mongodbartifect.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author mugeng.du
 */
@NoRepositoryBean
public interface BaseRepository<T,ID extends Serializable> extends MongoRepository<T,ID> {

}
