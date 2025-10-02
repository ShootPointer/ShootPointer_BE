package com.midas.shootpointer.domain.post.helper.elastic;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("es")
public class PostElasticSearchValidatorImpl implements PostElasticSearchValidator{
    @Override
    public boolean isHashTagSearch(String keyword) {
        return keyword.startsWith("#");
    }
}
