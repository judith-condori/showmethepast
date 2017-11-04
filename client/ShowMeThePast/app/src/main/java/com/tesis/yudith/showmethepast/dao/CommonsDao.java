package com.tesis.yudith.showmethepast.dao;

import com.tesis.yudith.showmethepast.configuration.NitriteManager;
import com.tesis.yudith.showmethepast.domain.collections.MongoCollection;

import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

public class CommonsDao {
    private NitriteManager nitriteManager;
    public final String ID_FIELD = "id";

    public CommonsDao(NitriteManager nitriteManager) {
        this.nitriteManager = nitriteManager;
    }

    public <T extends MongoCollection> ObjectRepository<T> getObjectRepository(Class<T> targetClass) {
        return this.nitriteManager.getDb().getRepository(targetClass);
    }

    public <T extends MongoCollection> Cursor<T> find(ObjectFilter filters, Class<T> targetClass) {
        ObjectRepository<T> objectRepository = this.getObjectRepository(targetClass);
        return objectRepository.find(filters);
    }

    public <T extends MongoCollection> T findOne(ObjectFilter filters, Class<T> targetClass) {
        return this.find(filters, targetClass).firstOrDefault();
    }

    public <T extends MongoCollection> T findOne(String id, Class<T> targetClass) {
        return this.find(ObjectFilters.eq(ID_FIELD, id), targetClass).firstOrDefault();
    }

    public <T extends MongoCollection> T findOne(Class<T> targetClass) {
        return this.find(NitriteManager.ALL_FILTER, targetClass).firstOrDefault();
    }

    public <T extends MongoCollection> void insert(T target, Class<T> targetClass) {
        ObjectRepository<T> objectRepository = this.getObjectRepository(targetClass);
        objectRepository.insert(target);
    }

    public <T extends MongoCollection> void update(T target, Class<T> targetClass) {
        ObjectRepository<T> objectRepository = this.getObjectRepository(targetClass);
        objectRepository.update(ObjectFilters.eq(ID_FIELD, target.getId()), target);
    }

    public <T extends MongoCollection> void remove(T target, Class<T> targetClass) {
        ObjectRepository<T> objectRepository = this.getObjectRepository(targetClass);
        objectRepository.remove(ObjectFilters.eq(ID_FIELD, target.getId()));
    }

    public <T extends MongoCollection> void remove(String _id, Class<T> targetClass) {
        ObjectRepository<T> objectRepository = this.nitriteManager.getDb().getRepository(targetClass);
        objectRepository.remove(ObjectFilters.eq(ID_FIELD, _id));
    }

    public <T extends MongoCollection> void remove(ObjectFilter filter, Class<T> targetClass) {
        ObjectRepository<T> objectRepository = this.nitriteManager.getDb().getRepository(targetClass);
        objectRepository.remove(filter);
    }
}
