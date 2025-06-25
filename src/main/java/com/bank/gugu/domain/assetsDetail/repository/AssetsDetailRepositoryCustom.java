package com.bank.gugu.domain.assetsDetail.repository;

import com.bank.gugu.domain.assetsDetail.repository.condition.AssetsCondition;
import com.bank.gugu.domain.assetsDetail.service.response.AssetsDetailResponse;
import com.bank.gugu.entity.assetsDetail.AssetsDetail;
import com.bank.gugu.entity.user.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssetsDetailRepositoryCustom {
    List<AssetsDetail> findByQuery(Pageable pageable, AssetsCondition condition, User user);
}
