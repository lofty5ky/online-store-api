package me.dev.onlinestoreapi.repository;

import me.dev.onlinestoreapi.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH o.orderDetails WHERE o.id = :id")
    Optional<Order> findById(@Param("id") Long orderId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderDetails WHERE o.user.id = :userId")
    List<Order> findByUserId(@Param("userId") Long userId);

    @Query("""
            SELECT o FROM Order o
            WHERE o.active = true AND
            (:keyword IS NULL OR :keyword = ''
            OR o.fullName LIKE %:keyword%
            OR o.address LIKE %:keyword%
            OR o.note LIKE %:keyword%
            OR o.email LIKE %:keyword%)
             """)
    @EntityGraph(attributePaths = {"orderDetails"})
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}