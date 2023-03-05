package com.zcx.cloud.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface CollectionUtil {
	
	/**
	 * Collection不为空并且Size大于零
	 * @param list
	 * @return
	 */
	public static boolean noNullAndSizeGtZero(Collection<? extends Object> list) {
		if(Objects.nonNull(list)&&list.size()>0)
			return true;
		return false;
	}
	
	/**
	 * List为空或者Size小于等于0
	 * @param list
	 * @return
	 */
	public static boolean isNullOrSizeLeZero(Collection<? extends Object> list) {
		if(Objects.isNull(list)||list.size()<=0)
			return true;
		return false;
	}
	
	/**
	 * 求两个集合的交集
	 * @param coll1
	 * @param coll2
	 * @return 返回两个集合的交集
	 */
	public static <T> Collection<T> intersection(Collection<T> coll1, Collection<T> coll2){
		Collection<T> in = new ArrayList<T>();
		coll1.forEach(c1 -> {
			coll2.forEach( c2 -> {
				if(c1.equals(c2)) {
					if(!in.contains(c1))
						in.add(c1);
				}
			});
		});
		
		return in;
	}
}
