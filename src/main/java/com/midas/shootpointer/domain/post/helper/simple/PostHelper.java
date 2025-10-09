package com.midas.shootpointer.domain.post.helper.simple;

import com.midas.shootpointer.domain.post.entity.PostEntity;
import java.util.List;
import java.util.UUID;

public interface PostHelper extends PostValidation, PostUtil {
	List<Long> findPostIdsByMemberId(UUID memberId);
	List<PostEntity> findPostsByPostIds(List<Long> postIds);
}
