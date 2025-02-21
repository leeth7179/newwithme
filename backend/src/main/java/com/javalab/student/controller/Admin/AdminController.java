package com.javalab.student.controller.Admin;

import com.javalab.student.constant.OrderStatus;
import com.javalab.student.dto.NewRegistrationDTO;
import com.javalab.student.dto.shop.TopSellingProductDTO;
import com.javalab.student.entity.shop.Item;
import com.javalab.student.repository.shop.OrderRepository;
import com.javalab.student.service.DoctorService;
import com.javalab.student.service.MemberService;
import com.javalab.student.service.StatisticsService;
import com.javalab.student.service.shop.ItemService;
import com.javalab.student.service.shop.SalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminController {
    private final DoctorService doctorService;
    private final MemberService memberService;
    private final StatisticsService statisticsService;
    private final SalesService salesService;
    private final ItemService itemService;
    @Autowired
    private OrderRepository orderRepository;




    /**
     * 전문가 리스트 전체 조회
     */
    // @GetMapping("/doctor/list")
    // public ResponseEntity<List<Doctor>> getDoctorList() {
    //     return ResponseEntity.ok(doctorService.getDoctorList());
    // }

    /**
     * 승인 대기중인 전문가 리스트 조회
     * - 전문가 상태가 대기, 보류, 거절인 전문가 리스트 조회
     */
    // @GetMapping("/doctor/pending")
    // public ResponseEntity<List<DoctorApplication>> getPendingDoctorList() {
    //     // 승인 대기중인 신청 리스트를 조회
    //     return ResponseEntity.ok(doctorService.getPendingDoctorApplicationList());
    // }

    /**
     * 전문가 상태변경
     */
    // @PutMapping("/doctor/approve/{email}")
    // public ResponseEntity<String> approveDoctor(
    //         @PathVariable String email,
    //         @RequestBody Map<String, String> requestBody) {

    //     // 요청 본문에서 상태(status)와 사유(reason)를 가져옴
    //     String status = requestBody.get("status");
    //     String reason = requestBody.get("reason"); // reason 추가

    //     // 상태 변경 처리
    //     doctorService.approveDoctorApplication(email, status, reason);

    //     return ResponseEntity.ok("Doctor application processed successfully");
    // }

    // 관리자 전체 상품 리스트 조회
    @GetMapping("/item/list")
    public ResponseEntity<List<Item>> getItemList() {
        return ResponseEntity.ok(itemService.getItemList());
    }

    // 일별 신규 가입자 수를 반환하는 API
    @GetMapping("/newRegistrations")
    public ResponseEntity<List<NewRegistrationDTO>> getNewRegistrationsPerDay() {
        log.info("일별 신규 가입자 데이터를 가져오는 중...");
        List<NewRegistrationDTO> newRegistrations = statisticsService.getNewRegistrationsPerDay();
        log.info("일별 신규 가입자 수: {}", newRegistrations.size());
        return ResponseEntity.ok(newRegistrations);
    }

    // 일별 신규 전문가 신청 수를 반환하는 API
    @GetMapping("/newDoctorApplications")
    public ResponseEntity<List<NewRegistrationDTO>> getNewDoctorApplicationsPerDay() {
        log.info("일별 신규 전문가 신청 데이터를 가져오는 중...");
        List<NewRegistrationDTO> newDoctorApplications = statisticsService.getNewDoctorApplicationsPerDay();
        log.info("일별 신규 전문가 신청 수: {}", newDoctorApplications.size());
        return ResponseEntity.ok(newDoctorApplications);
    }

    // 판매량 상위 5개 집계 API
    @GetMapping("/topSellingProducts/{status}")
    public ResponseEntity<List<TopSellingProductDTO>> getTopSellingProducts(@PathVariable OrderStatus status) {
        log.info("상태 {}에 해당하는 판매량 상위 5개 상품을 가져오는 중...", status);

        // ORDER_COMPLETED 상태의 주문 ID 가져오기
        List<Long> completedOrderIds = orderRepository.findCompletedOrderIds(status);
        log.info("상태 {}에 해당하는 완료된 주문 ID: {}", status, completedOrderIds.size());

        // 판매량 집계
        Map<Long, Integer> salesCount = salesService.getSalesCount(completedOrderIds);
        log.info("상품별 판매량 집계 완료: {}개의 상품", salesCount.size());

        // 상위 5개 아이템 가져오기
        List<TopSellingProductDTO> topSellingProducts = salesService.getTopSellingProducts(salesCount);
        log.info("상위 5개 판매 상품 정보 가져옴: {}", topSellingProducts.size());

        return ResponseEntity.ok(topSellingProducts);
    }

    // 일 매출 금액 계산
    @GetMapping("/dailySales/{status}")
    public ResponseEntity<Double> getDailySales(
            @PathVariable OrderStatus status,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        log.info("상태 {}에 해당하는 {} 일 매출 금액 계산 중...", status, date);

        // 해당 날짜의 주문 ID 가져오기
        List<Long> completedOrderIds = orderRepository.findCompletedOrderIds(status);
        log.info("일 매출 계산을 위한 완료된 주문 ID: {}", completedOrderIds.size());

        // 일 매출 계산
        double dailySalesAmount = salesService.getDailySalesAmount(completedOrderIds, date);
        log.info("계산된 {} 일 매출 금액: {}", date, dailySalesAmount);

        return ResponseEntity.ok(dailySalesAmount);
    }

    // 월 매출 금액 계산
    @GetMapping("/monthlySales/{status}")
    public ResponseEntity<Double> getMonthlySales(@PathVariable OrderStatus status,
                                                  @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                  @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("상태 {}에 해당하는 월 매출 금액을 {}부터 {}까지 계산 중...", status, startDate, endDate);

        // ORDER_COMPLETED 상태의 주문 ID 가져오기
        List<Long> completedOrderIds = orderRepository.findCompletedOrderIds(status);
        log.info("월 매출 계산을 위한 완료된 주문 ID: {}", completedOrderIds.size());

        // 월 매출 계산
        double monthlySalesAmount = salesService.getMonthlySalesAmount(completedOrderIds, startDate, endDate);
        log.info("계산된 월 매출 금액: {}", monthlySalesAmount);

        return ResponseEntity.ok(monthlySalesAmount);
    }
}