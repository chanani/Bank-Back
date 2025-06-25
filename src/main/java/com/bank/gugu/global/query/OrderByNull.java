package com.bank.gugu.global.query;

import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

/**
 * order by 동적 사용시 만약 없는 경우 null 대신 처리
 * */
public class OrderByNull extends OrderSpecifier {

    private static final OrderByNull DEFAULT = new OrderByNull();

    private OrderByNull() {
        super(Order.ASC, NullExpression.DEFAULT, NullHandling.Default);
    }

    public static OrderByNull getDefault() {
        return OrderByNull.DEFAULT;
    }
}