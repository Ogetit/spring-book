package com.github.core.authorization;

import org.github.core.modules.exception.BusinessException;

/**
 * Created by github on 2017/6/6.
 */
public interface Traffic {

    void  checkTrafficOverProof(String url) throws BusinessException;
}
