package com.pfh.crm.dashboard.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.pfh.crm.dashboard.common.annotaition.CreateTime;
import com.pfh.crm.dashboard.common.annotaition.Delete;
import com.pfh.crm.dashboard.common.annotaition.UpdateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 新增、修改自动注入时间
 *
 * @author pengfh
 * @see CreateTime 创建时间，新增时自动注入，修改不变
 * @see UpdateTime 修改时间，修改时自动注入
 * @see Delete 删除标识，新增时自动注入
 */
@Slf4j
@Component
public class ModifyHandler implements MetaObjectHandler {


    /**
     * 新增数据执行
     * @param metaObject 数据库操作对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        Field[] declaredFields = metaObject.getOriginalObject().getClass().getDeclaredFields();
        Field[] superFields = metaObject.getOriginalObject().getClass().getSuperclass().getDeclaredFields();
        Set<Field> fields = new HashSet<>(declaredFields.length+superFields.length);
        CollectionUtils.mergeArrayIntoCollection(declaredFields,fields);
        CollectionUtils.mergeArrayIntoCollection(superFields,fields);

        for (Field declaredField : fields) {
            processCreateTime(metaObject,declaredField);
            processUpdateTime(metaObject,declaredField);
            processDelete(metaObject,declaredField);
        }
    }

    /**
     * 更新数据执行
     * @param metaObject 数据库操作对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        MapperMethod.ParamMap map = (MapperMethod.ParamMap) metaObject.getOriginalObject();
        Field[] declaredFields = map.get("param1").getClass().getDeclaredFields();
        Field[] superFields = map.get("param1").getClass().getSuperclass().getDeclaredFields();
        Set<Field> fields = new HashSet<>(declaredFields.length+superFields.length);
        CollectionUtils.mergeArrayIntoCollection(declaredFields,fields);
        CollectionUtils.mergeArrayIntoCollection(superFields,fields);
        for (Field declaredField : fields) {
            processUpdateTime(metaObject,declaredField);
        }
    }

    /**
     * 注入更新时间，如果字段有{@link CreateTime} 注解
     *
     * @param metaObject 数据库操作对象
     * @param field 更新字段
     */
    private void processCreateTime(MetaObject metaObject,Field field){
        boolean createTimePresent = field.isAnnotationPresent(CreateTime.class);
        if (createTimePresent) {
            CreateTime annotationCreateTime = field.getAnnotation(CreateTime.class);

            String createTimeValue = annotationCreateTime.value();
            String createTimeFieldName;
            if (StringUtils.isEmpty(createTimeValue)){
                createTimeFieldName = field.getName();
            } else {
                createTimeFieldName = createTimeValue;
            }
            this.setFieldValByName(createTimeFieldName, new Date(), metaObject);
        }
    }

    /**
     * 注入更新时间，如果字段有{@link UpdateTime} 注解
     *
     * @param metaObject 数据库操作对象
     * @param field 更新字段
     */
    private void processUpdateTime(MetaObject metaObject,Field field){
        boolean updateTimePresent = field.isAnnotationPresent(UpdateTime.class);
        if (updateTimePresent) {
            UpdateTime annotationUpdateTime = field.getAnnotation(UpdateTime.class);
            String updateTimeValue = annotationUpdateTime.value();
            String updateTimeFieldName;
            if (StringUtils.isEmpty(updateTimeValue)) {
                updateTimeFieldName = field.getName();
            } else {
                updateTimeFieldName = updateTimeValue;
            }
            this.setFieldValByName(updateTimeFieldName, new Date(), metaObject);
        }
    }

    /**
     * 注入更新时间，如果字段有{@link Delete} 注解
     *
     * @param metaObject 数据库操作对象
     * @param field 更新字段
     */
    private void processDelete(MetaObject metaObject,Field field){
        boolean deletePresent = field.isAnnotationPresent(Delete.class);
        if (deletePresent) {
            Delete annotationDelete = field.getAnnotation(Delete.class);
            String deleteValue = annotationDelete.value();
            String deleteFieldName;
            if (StringUtils.isEmpty(deleteValue)) {
                deleteFieldName = field.getName();
            } else {
                deleteFieldName = deleteValue;
            }

            this.setFieldValByName(deleteFieldName, 1, metaObject);
        }
    }


}