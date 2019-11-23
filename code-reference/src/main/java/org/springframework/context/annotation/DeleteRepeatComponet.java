package org.springframework.context.annotation;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by github on 2017/3/16.
 */
public class DeleteRepeatComponet {
    private final static Log logger = LogFactory.getLog(DeleteRepeatComponet.class);
    private static BeanNameGenerator beanNameGenerator;

    private DeleteRepeatComponet() {

    }

    public static void deleteRepeat(Map<String, BeanDefinition> candiateMap, Set<BeanDefinition> candidates,
                                    ScannedGenericBeanDefinition sbd) throws Exception {
        if (beanNameGenerator == null) {
            beanNameGenerator = new AnnotationBeanNameGenerator();
        }
        String beanName = beanNameGenerator.generateBeanName(sbd, null);
        beanName = StringUtils.lowerCase(beanName);
        BeanDefinition existBean = candiateMap.get(beanName);
        if (existBean == null) {
            candiateMap.put(beanName, sbd);
            candidates.add(sbd);
        } else {
            File existBeanFile = ((Resource) existBean.getSource()).getFile();
            File currentBeanFile = ((Resource) sbd.getSource()).getFile();
            if (existBeanFile.lastModified() > currentBeanFile.lastModified()) {//当前class旧了
                currentBeanFile.delete();
                logger.error("删除了重名的类【" + beanName + "】" + currentBeanFile.getPath());
            } else {
                candiateMap.put(beanName, sbd);
                candidates.remove(existBean);
                candidates.add(sbd);
                existBeanFile.delete();
                logger.error("删除了重名的类【" + beanName + "】" + currentBeanFile.getPath());
            }
        }
    }
}
