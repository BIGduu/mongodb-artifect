package com.bigduu.mongodbartifect.service;

import com.bigduu.mongodbartifect.repository.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public class BaseService<T,ID extends Serializable> implements BaseRepository<T,ID> {

    @Autowired
    private BaseRepository<T,ID> repository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public <S extends T> S save(S s) {
        return repository.save(s);
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> iterable) {
        return repository.findAllById(iterable);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(T t) {
        repository.delete(t);
    }

    @Override
    public void deleteAll(Iterable<? extends T> iterable) {
        repository.deleteAll(iterable);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public <S extends T> S insert(S entity) {
        return repository.insert(entity);
    }

    @Override
    public <S extends T> List<S> insert(Iterable<S> entities) {
        return repository.insert(entities);
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return repository.findOne(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return repository.findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example , Sort sort) {
        return repository.findAll(example,sort);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example , Pageable pageable) {
        return repository.findAll(example,pageable);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return repository.count(example);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return repository.exists(example);
    }
}
