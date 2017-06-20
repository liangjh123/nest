package com.jovezhao.nest.ddd.builder;

import com.jovezhao.nest.ddd.BaseEntityObject;
import com.jovezhao.nest.ddd.Identifier;

public class FactoryBuilder<T extends BaseEntityObject> implements IBuilder<T> {
    Class<T> tClass;

    public FactoryBuilder(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T build(Identifier id) {
        T t = EntityObjectFactory.create(tClass);
        t.setId(id);
        return t;
    }

    @Override
    public <U extends T> U build(Class<U> uClass, Identifier id) {
        U u = EntityObjectFactory.create(uClass);
        u.setId(id);
        return u;
    }
}
