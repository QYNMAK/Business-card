package com.pfh.crm.dashboard.common.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArrayListUtil {

    public static <T> List<T> copy(Collection src, Class<T> typeType) {
        List<T> target = new ArrayList<>(src.size());
        for (Object o : src) {
            T t = BeanUtils.instantiateClass(typeType);
            BeanUtils.copyProperties(o, t);
            target.add(t);
        }
        return target;
    }
}
