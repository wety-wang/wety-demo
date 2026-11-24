package com.wety.demo.core.misc;

import com.wety.demo.common.util.ObjectUtils;
import com.wety.demo.core.annotation.ModifyRecordField;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * 工具类，对比两个对象属性的变化
 *
 * @author wety
 *
 */
public class FieldModificationCollector {
    private FieldModificationCollector() {
    }

    /**
     * 提取两个对象属性值的变化
     *
     * @param type   Class类型，用于标识哪些属性变化需要提取
     * @param source 原对象
     * @param target 目标对象
     * @return
     * @throws Exception
     */
    public static List<FieldModificationItem> collectModifications(Class<?> type, Object source, Object target)
            throws Exception {
        List<FieldModificationItem> items = new ArrayList<>();
        List<ModifyRecordFieldHolder> modifyRecordFieldHolders = ModifyRecordFieldExtractor.extract(type);
        for (ModifyRecordFieldHolder holder : modifyRecordFieldHolders) {
            if (!holder.getModifyRecordField().logable()) {
                continue;
            }
            FieldModificationItem item = new FieldModificationItem();
            Object oldValue = PropertyUtils.getProperty(source, holder.getFieldName());
            Object newValue = PropertyUtils.getProperty(target, holder.getFieldName());
            if (!ObjectUtils.equals(oldValue, newValue) || !holder.modifyRecordField.ignoreNoChange()) {
                item.setLabel(holder.getModifyRecordField().label());
                item.setOldValue(Objects.toString(ObjectUtils.stripTrailingZerosIfBigDecimal(oldValue) , ""));
                item.setNewValue(Objects.toString(ObjectUtils.stripTrailingZerosIfBigDecimal(newValue), ""));
                items.add(item);
            }
        }
        return items;
    }

    public static String collectAsString(Class<?> type, Object source, Object target) throws Exception {
        List<FieldModificationItem> list = collectModifications(type, source, target);
        StringBuilder info = new StringBuilder();
        Iterator<FieldModificationItem> iterator = list.iterator();
        if (iterator.hasNext()) {
            FieldModificationItem item = iterator.next();
            info.append(item.toString());
        }
        while (iterator.hasNext()) {
            FieldModificationItem item = iterator.next();
            info.append("; ");
            info.append(item.toString());
        }
        return info.toString();
    }

    /**
     * ModifyRecordField注解提取器，属性上的注解提取为ModifyRecordFieldHolder
     *
     * @author wety
     *
     */
    static class ModifyRecordFieldExtractor {
        private ModifyRecordFieldExtractor() {
        }

        /**
         * 从指定的类型里提取属性上的ModifyRecordField注解
         *
         * @param type Class<?>
         * @return List<ModifyRecordFieldHolder>
         */
        public static List<ModifyRecordFieldHolder> extract(Class<?> type) {
            List<ModifyRecordFieldHolder> list = new ArrayList<>();
            Field[] declaredFields = type.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(ModifyRecordField.class)) {
                    ModifyRecordFieldHolder holder = new ModifyRecordFieldHolder();
                    holder.setModifyRecordField(field.getAnnotation(ModifyRecordField.class));
                    holder.setFieldName(field.getName());
                    list.add(holder);
                }
            }
            return list;
        }
    }

    /**
     * holder for ModifyRecordField and field name
     *
     * @author wety
     *
     */
    static class ModifyRecordFieldHolder {
        private ModifyRecordField modifyRecordField;
        private String fieldName;

        public ModifyRecordField getModifyRecordField() {
            return modifyRecordField;
        }

        public void setModifyRecordField(ModifyRecordField modifyRecordField) {
            this.modifyRecordField = modifyRecordField;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

    }

    /**
     * 属性更改条目：[label：oldValue->newValue]
     *
     * @author wety
     *
     */
    static class FieldModificationItem {
        private String label;
        private String oldValue;
        private String newValue;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            StringBuilder info = new StringBuilder();
            info.append(this.label);
            info.append(": ");
            info.append(this.oldValue);
            info.append("->");
            info.append(this.newValue);
            return info.toString();
        }
    }

    /**
     *
     * @author wety
     *
     */
    public static class DemoPerson {
        @ModifyRecordField(label = "数据主键", logable = false)
        private Long id;

        @ModifyRecordField(label = "业务编码", logable = false)
        private String code;

        @ModifyRecordField(label = "姓名", logable = true, ignoreNoChange = false)
        private String name;

        @ModifyRecordField(label = "年龄", logable = true, ignoreNoChange = false)
        private Integer age;

        @ModifyRecordField(label = "身价", logable = true, ignoreNoChange = false)
        private BigDecimal worth;

        @ModifyRecordField(label = "创建时间", logable = false)
        private Date createTime;

        @ModifyRecordField(label = "更新时间", logable = false)
        private Date updateTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public BigDecimal getWorth() {
            return worth;
        }

        public void setWorth(BigDecimal worth) {
            this.worth = worth;
        }

    }

    public static void main(String[] args) {
        DemoPerson oldPerson = new DemoPerson();
        oldPerson.setId(1L);
        oldPerson.setCode("1");
        oldPerson.setName("冯大成");
        oldPerson.setAge(31);
        oldPerson.setCreateTime(new Date());
        oldPerson.setUpdateTime(new Date());
        DemoPerson newPerson = new DemoPerson();
        newPerson.setId(1L);
        newPerson.setCode("1");
        newPerson.setName("冯小刚");
        newPerson.setAge(32);
        newPerson.setCreateTime(new Date());
        newPerson.setUpdateTime(new Date());
    }
}
