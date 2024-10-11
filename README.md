# Senior2Project
실무프로젝트 

User 
- domain : users 테이블의 정보를 담고 있음
- dto : service 와 domain 간의 매핑
- repository : orm 사용
- service : Sign - 로그인  User : 회원가입, 조회 등등
- controller : 서비스 호출 

Item
- domain 
- dto
- repository
- service
- controller
  - category : 메인 페이지 및 상품 등록에서 카테고리 목록을 보여주는 API
  - Item : 상품 등록 API
  - Order : 상품 구매 및 취소, 배송 상태 변경 등 API
  - Review : 리뷰를 작성 및 관리하고, 리뷰 작성을 위한 조건을 관리한다.

libs
- Database : JDBCTemplate 를 이용하여 직접 쿼리문을 작성해 사용 가능하도록 라이브러리 구현 



TODO
<24-09-30>
- 상품 배송 출발 상태 변경 (OrderService, OrderApiController) ✅
- 리뷰 수정, 리뷰 삭제 ✅
- 관리자 측면, 판매 물품 관리, 주문 관리 -> 배송 출발 상태 변경  ✅
- 메인 페이지 API ( 모든 상품 보여주기 ) ✅
- libs - DataBase : 둘 다 private 로 변경하고, 들어오는 쿼리문에 따라 각 상황에 맞는 함수가 호출되도록 ✅ 

<24-10-02>
- 내가 쓴 리뷰 리스트 모아보기 ✅
- 운영자 관점에서 상품마다 리뷰 모아보기 ✅ ( 상품 별, 상품 후기 별 조회 ) 
- 메인 페이지 API ( 모든 상품 보여주기 ) ✅
- 관리자 측면, 판매 물품 관리, 주문 관리

<24-10-06>
- 관리자 측면 : 판매 물품 관리 (삭제✅), 주문 관리 ✅
- 상품 상세 페이지 조회 (이미지와 같이 조회) ✅

<24-10-10>
- 메인 페이지 필터링 기능 ✅