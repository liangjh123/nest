package com.zhaofujun.nest.ioc;

import com.zhaofujun.nest.NestApplication;
import net.sf.cglib.proxy.Enhancer;

public class ServiceBeanBuilder {

    private BeanContainer beanContainer;


    public ServiceBeanBuilder(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public Object create(Class<?> serviceClass) {

        NestApplication application = beanContainer.getInstance(NestApplication.class);

        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new ApplicationServiceInterceptor(application));
        return enhancer.create();

    }
}

