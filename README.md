# Tamang Food Service

## Mô tả ứng dụng

Tamang Food Service là một ứng dụng Android phục vụ đặt món ăn trực tuyến. Người dùng có thể xem danh mục món, tìm kiếm, thêm vào giỏ hàng, thanh toán, theo dõi đơn hàng và quản lý thông tin cá nhân.

Ứng dụng có quy trình: onboarding -> đăng nhập/đăng ký -> trang chính với điều hướng Bottom Navigation -> các chức năng đặt đồ ăn và theo dõi đơn.

## Chức năng chính

- Onboarding giới thiệu tính năng trước khi vào ứng dụng
- Đăng ký, đăng nhập, đặt lại mật khẩu và xác thực OTP
- Xem danh mục món ăn, danh sách yêu thích và đề xuất
- Xem chi tiết món, hình ảnh, thành phần, đánh giá và thêm vào giỏ hàng
- Giỏ hàng với thay đổi số lượng, xóa mục và tạo đơn hàng
- Thanh toán với Stripe
- Theo dõi tiến độ giao hàng và nhận thông báo khi đơn hàng thay đổi
- Quản lý địa chỉ giao hàng và thông tin người dùng
- Hủy đơn, đánh giá sau khi đặt

## Kiến trúc và kỹ thuật sử dụng

- Ngôn ngữ: Kotlin
- Kiến trúc: phân tách rõ ràng theo layers
  - `presentation` cho UI và navigation
  - `domain` chứa use case, model và interface repository
  - `data` chứa implementation repository, API, mô hình dữ liệu
  - `di` cấu hình Hilt để inject dependency
- Navigation Component để điều hướng giữa các fragment
- ViewBinding để ánh xạ layout an toàn
- Dagger Hilt cho dependency injection
- Retrofit + OkHttp + Gson cho gọi API mạng
- Android Paging cho danh sách dữ liệu lớn
- WorkManager cho thông báo tiến độ đơn hàng
- Stripe SDK cho thanh toán
- Biometric API cho xác thực
- osmdroid cho bản đồ và định vị
- Glide để tải ảnh từ URL

## Cấu trúc mã nguồn

- `FoodApplication.kt`
  - Khởi tạo Stripe và `AppPreferences`
- `MainActivity.kt`
  - Điều khiển start destination theo trạng thái đăng nhập
  - Xử lý truy cập từ push notification và chuyển tới màn hình theo dõi đơn
  - Quan sát session expired để buộc logout
- `presentation/ui/onboarding`
  - `OnboardingFragment` + adapter cho loạt màn hình giới thiệu
- `presentation/ui/authentication`
  - Container cho các màn hình xác thực: đăng nhập, đăng ký, quên mật khẩu, OTP
- `presentation/ui/mainapp`
  - `MainAppFragment` có Bottom Navigation
  - `main_navigation` chứa các màn hình chính trong app
- `presentation/ui/mainapp/home`
  - Hiển thị danh mục, bestseller, gợi ý, giỏ hàng nhanh
- `presentation/ui/mainapp/menu`
  - Duyệt thực đơn theo danh mục và lọc món
- `presentation/ui/mainapp/fooddetail`
  - Màn hình chi tiết món ăn, thành phần và đánh giá
- `presentation/ui/mainapp/order`
  - Quản lý đơn hàng, hủy đơn, theo dõi và đánh giá

## Ai chịu trách nhiệm phần nào

- Giao diện (UI)
  - `presentation` chịu trách nhiệm layout, interaction, điều hướng giữa các fragment
  - Các fragment như `OnboardingFragment`, `AuthenticationFragment`, `MainAppFragment`, `HomeFragment`, `MenuFragment`, `OrderFragment`, `FoodDetailFragment` đảm nhiệm phần giao diện
- Logic nghiệp vụ
  - `domain/usecase` chứa các business use case như thêm sản phẩm, tạo đơn, lấy danh mục, quản lý tài khoản
  - `data/repository` triển khai logic lấy dữ liệu từ API hoặc nguồn dữ liệu
  - `di` cung cấp cấu hình dependency, gắn kết repository và use case với UI
  - `presentation/utils` chứa hỗ trợ session, thông báo và tiện ích chung

## Các module kỹ thuật cụ thể

- `data/repository/*RepositoryImpl.kt`
  - Chịu trách nhiệm gọi API, xử lý dữ liệu từ tầng mạng và trả về cho domain
- `domain/usecase/*UseCase.kt`
  - Tách các hành động nghiệp vụ thành các use case riêng biệt
- `di/ApiModule.kt`, `di/RepositoryModule.kt`, `di/UseCaseModule.kt`
  - Đăng ký các dependency dùng chung trong ứng dụng
- `AndroidManifest.xml`
  - Yêu cầu quyền: Internet, biometric, location, storage và notifications

## Ghi chú

- `local.properties` lưu `stripe.publishableKey` và `baseUrl` để tách cấu hình môi trường khỏi mã nguồn.
- Ứng dụng hiện chạy trên Android SDK 24+ và compile SDK 36.
