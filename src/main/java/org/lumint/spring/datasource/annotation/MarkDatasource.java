/**
* Copyright © 2015 meilishuo.com All rights reserved.
*/
package org.lumint.spring.datasource.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**  
 * @Title: MarkDatasource.java

 * @Prject: datasource

 * @Package: org.lumint.spring.datasource.annotation

 * @Description: see class name

 * @author: weizhong

 * @date: Jan 2, 2015 5:09:44 PM

 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface MarkDatasource {
	String value();
}
