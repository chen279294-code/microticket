package com.example.microticket.repository;

import com.example.microticket.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 原有方法：保留
    Optional<Order> findByOrderNo(String orderNo);

    // ================== 管理员 Dashboard 用 ==================

    /** 订单总数（你也可以直接用 JpaRepository.count()，保留也行） */
    @Query("select count(o) from Order o")
    long countOrders();

    /** 已支付订单总金额（总票房） */
    @Query(value = "select coalesce(sum(o.total_amount),0) from orders o where o.status='PAID'", nativeQuery = true)
    BigDecimal sumTotalPaid();

    /** 今日已支付订单总金额（今日票房） */
    @Query(value = "select coalesce(sum(o.total_amount),0) from orders o where o.status='PAID' and date(o.created_at)=curdate()", nativeQuery = true)
    BigDecimal sumTodayPaid();

    /** 近7天每日票房：返回 [date, sum]，没有订单的日期由 Service 补 0 */
    @Query(value =
            "select date(o.created_at) as d, coalesce(sum(o.total_amount),0) as s " +
                    "from orders o " +
                    "where o.status='PAID' and o.created_at >= :start " +
                    "group by date(o.created_at) " +
                    "order by d asc",
            nativeQuery = true)
    List<Object[]> sumPaidByDaySince(@Param("start") LocalDateTime start);

    /**
     * 按电影汇总票房：返回 [movie.description, sum]
     * 用于 Service 再按“类型”聚合（从 description 推断类型）
     */
    @Query(value =
            "select m.description as desc_text, coalesce(sum(o.total_amount),0) as s " +
                    "from orders o " +
                    "join screenings sc on o.screening_id = sc.id " +
                    "join movies m on sc.movie_id = m.id " +
                    "where o.status='PAID' " +
                    "group by m.description",
            nativeQuery = true)
    List<Object[]> sumPaidByMovie();
}
