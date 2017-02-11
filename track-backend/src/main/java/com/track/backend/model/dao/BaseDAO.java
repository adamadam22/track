package com.track.backend.model.dao;

import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseDAO<T> {

	@PersistenceContext
	protected EntityManager em;	
	
	protected Class<T> persistentClass;
	
	@SuppressWarnings("unchecked")
	public BaseDAO() {
		persistentClass = (Class<T>) ((ParameterizedType) (Enhancer.isEnhanced(getClass())
				? getClass().getSuperclass() : getClass()).getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	public T findById(Object id) {
		return em.find(persistentClass, id);
	}
	
	@Transactional
	public void add(Object object){
		em.persist(object);
	}
	
	@Transactional
	public void update(Object object){
		em.merge(object);
	}
	
	@Transactional
	public void delete(Object object){
		em.remove(object);
	}

}
