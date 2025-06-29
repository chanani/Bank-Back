package com.bank.gugu.domain.icon.service;

import com.bank.gugu.domain.icon.repository.IconRepository;
import com.bank.gugu.domain.icon.service.dto.response.IconsResponse;
import com.bank.gugu.entity.common.constant.StatusType;
import com.bank.gugu.entity.icon.Icon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DefaultIconService implements IconService {

    private final IconRepository iconRepository;

    @Override
    public List<IconsResponse> getIcons() {
        return iconRepository.findAllByStatus(StatusType.ACTIVE).stream()
                .map(IconsResponse::new)
                .toList();
    }
}
