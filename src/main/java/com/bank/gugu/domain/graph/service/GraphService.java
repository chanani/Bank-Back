package com.bank.gugu.domain.graph.service;

import com.bank.gugu.controller.graph.input.GraphsInput;
import com.bank.gugu.domain.graph.service.dto.response.GraphsResponse;
import com.bank.gugu.entity.user.User;

public interface GraphService {
    GraphsResponse getGraphs(GraphsInput input, User user);
}
