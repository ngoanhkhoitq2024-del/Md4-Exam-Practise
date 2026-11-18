# README – MICROSERVICES ORDER & INVENTORY

## 1. Giải thích cơ chế Service Discovery

**Service Discovery** là cơ chế cho phép các Microservice tự đăng ký và tìm kiếm địa chỉ của nhau thông qua một hệ thống quản lý tập trung gọi là Service Registry.

Trong hệ thống này, Eureka Server đóng vai trò Service Registry:
- Khi khởi động, các Service như API Gateway, Order Service và Inventory Service đăng ký thông tin của mình với Eureka.
- Khi cần giao tiếp, Gateway hoặc Order Service sử dụng tên định danh của Service để tìm kiếm instance tương ứng thông qua Eureka.

**Tại sao Gateway không nên gọi trực tiếp địa chỉ IP/Port?**

- IP/Port có thể thay đổi khi Service khởi động lại hoặc triển khai thêm instance.
- Khi mở rộng số lượng instance, phải cập nhật lại địa chỉ nếu cấu hình cố định.
- Khó cân bằng tải và quản lý các Service khi hệ thống phát triển.

Sử dụng Eureka giúp Gateway tìm kiếm Service thông qua tên định danh thay vì địa chỉ IP/Port cố định, đồng thời hỗ trợ Load Balancing và giúp hệ thống linh hoạt, dễ bảo trì, mở rộng hơn.

## 2. Mở rộng hệ thống (Scaling) khi số lượng request tăng đột biến

Sử dụng phương pháp **Horizontal Scaling (mở rộng theo chiều ngang)** bằng cách triển khai thêm nhiều instance của Order Service trên các Port khác nhau.

Ví dụ:
- Order Service Instance 1: Port 8081.
- Order Service Instance 2: Port 8083.
- Order Service Instance 3: Port 8084.

Các instance cùng đăng ký với Eureka Server dưới tên `ORDER-SERVICE`.

API Gateway vẫn định tuyến request thông qua tên Service `order-service`, không cần thay đổi cấu hình khi thêm hoặc bớt instance.

Eureka kết hợp với Spring Cloud LoadBalancer sẽ hỗ trợ phát hiện các instance và phân phối request giữa chúng.

**Kết quả:** Tăng khả năng xử lý request đồng thời, giảm tải cho từng instance và mở rộng hệ thống mà không cần chỉnh sửa cấu hình Gateway.

## 3. So sánh OpenFeign (Đồng bộ) và Kafka (Bất đồng bộ) trong bài toán đặt hàng

### a. OpenFeign (Đồng bộ)

**Ưu điểm:**
- Order Service gọi Inventory Service và nhận kết quả ngay trong cùng một luồng xử lý.
- Có thể kiểm tra sản phẩm, tồn kho và quyết định tạo đơn hàng ngay lập tức.
- Dễ triển khai, dễ kiểm soát quy trình đặt hàng và xử lý lỗi.

**Nhược điểm:**
- Order Service phải chờ Inventory Service phản hồi mới tiếp tục xử lý.
- Nếu Inventory Service bị lỗi hoặc quá tải, quá trình đặt hàng có thể bị gián đoạn.
- Khi có nhiều request đồng thời, các lời gọi đồng bộ có thể làm tăng thời gian chờ và tải cho hệ thống.

### b. Kafka (Bất đồng bộ)

**Ưu điểm:**
- Order Service gửi yêu cầu qua Kafka mà không phải chờ Inventory Service xử lý ngay lập tức.
- Kafka có khả năng lưu trữ message, giúp tiếp nhận và xử lý lượng lớn yêu cầu khi tải tăng đột biến.
- Giảm sự phụ thuộc trực tiếp giữa Order Service và Inventory Service.

**Nhược điểm:**
- Không nhận được kết quả kiểm tra tồn kho ngay lập tức; cần cơ chế nhận phản hồi và cập nhật trạng thái đơn hàng.
- Phải xử lý các trường hợp hết hàng, đặt hàng thất bại và hoàn tác dữ liệu.
- Cần quản lý message trùng lặp, retry và tính nhất quán dữ liệu giữa các Service.

### c. Lựa chọn phương thức giao tiếp

Trong bài toán trên, **OpenFeign** là phù hợp vì quá trình đặt hàng cần nhận kết quả kiểm tra tồn kho từ Inventory Service ngay lập tức trước khi quyết định tạo đơn hàng.

OpenFeign phù hợp với yêu cầu xử lý đồng bộ, dễ triển khai và kiểm thử trong phạm vi bài thực hành.

Kafka sẽ phù hợp hơn nếu hệ thống cần xử lý các nghiệp vụ bất đồng bộ với lượng giao dịch lớn, chẳng hạn như gửi thông báo đơn hàng, cập nhật báo cáo hoặc thống kê.