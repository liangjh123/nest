package com.jovezhao.nest.ddd;

import com.jovezhao.nest.ddd.builder.FactoryBuilder;
import com.jovezhao.nest.ddd.builder.RepositoryLoader;
import com.jovezhao.nest.ddd.identifier.IdGenerator;
import com.jovezhao.nest.log.ILog;
import com.jovezhao.nest.log.LogAdapter;
import com.jovezhao.nest.utils.SpringUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaofujun on 2017/6/20.
 */
public abstract class BaseEntityObject<T extends Identifier> implements Serializable {
    private transient boolean isLoad;

    private transient ILog logger = new LogAdapter(this.getClass());
    /**
     * 唯一ID
     */
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    protected boolean OnVerifiying() {
        return true;
    }

    /**
     * 将自己扮演成一个具体的角色以执行一个操作
     * 如果roleid为空时，将创建一个新的角色信息
     * 如果指定的roleid在仓储中不存在，将新建一个角色供他使用
     *
     * @param clazz  要扮演的角色的类型
     * @param roleId 如果不为空将通过仓储加载一个数据对象
     * @param <T>
     * @return
     */
    public <T extends BaseRole> T act(Class<T> clazz, Identifier roleId) {

        T t = null;
        try {

            if (!StringUtils.isEmpty(roleId)) {
                t = new RepositoryLoader<>(clazz).build(roleId);
            } else {
                roleId = IdGenerator.getInstance().generate(clazz);
            }
            if (t == null) {
                t = new FactoryBuilder<>(clazz).build(roleId);
            }
            t.setActor(this);

        } catch (Exception e) {
            logger.fatal(e, clazz, roleId);
        }

        return t;
    }

    public <T extends BaseRole> Set<T> findRoles(Class<T> clazz) {

        IRoleRepository<T> repository = (IRoleRepository<T>) RepositoryManager.getEntityRepository(clazz);
        Set<Identifier> ids = repository.getRoleIds(this.id);
        Set<T> tSet = new HashSet<>();
        for (Identifier id : ids) {
            T t = this.act(clazz, id);
            tSet.add(t);
        }
        return tSet;
    }

    /**
     * 获取一个默认的角色，如果仓储中不存在这个角色时将创建一个角色
     * @param tClass
     * @param <T>
     * @return
     */
    public <T extends BaseRole> T act(Class<T> tClass) {
        return act(tClass,this.getId());
    }

    private void addToUnitOfWork() {
        IUnitOfWork unitOfWork = SpringUtils.getInstance(IUnitOfWork.class);
        if (unitOfWork != null)
            unitOfWork.addEntityObject(this);
    }


    public void delete() {
        IUnitOfWork unitOfWork = SpringUtils.getInstance(IUnitOfWork.class);
        if (unitOfWork != null)
            unitOfWork.removeEntityObject(this);
    }
}
