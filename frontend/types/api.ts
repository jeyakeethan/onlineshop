export interface CategoryDTO { category?: string; subcategory?: string }
export interface InventoryDTO { inventoryId?: number; skuId?: number; costPrice?: number; sellingPrice?: number; stockAvailable?: number }
export interface SKUDTO { size?: string; color?: string; skuCode?: string; productId?: number; inventoryDTOS?: InventoryDTO[] }
export interface ProductDTO { productId?: number; name: string; description?: string; categoryDTO?: CategoryDTO; skuDTOs?: SKUDTO[]; isAvailable?: boolean; approved?: boolean }
export interface AuthenticationRequest { userId: string; password: string }
export interface AuthenticationResponse { token: string }
export interface UserDTO { email: string; firstName?: string; lastName?: string; password?: string; dob?: string; roles?: string[]; lastLoginTime?: string }
export interface RoleDTO { id?: number; name?: string; permissions?: string[] }
export interface CartItemRequestDTO { inventoryId: number; quantity: number }
export interface CartItemDTO { cartItemId?: number; inventoryId?: number; quantity?: number; priceAtAdd?: number }
export interface CartDTO { cartId?: number; userId?: string; cartItems?: CartItemDTO[] }
export interface AddressDTO { id?: number; street?: string; city?: string; state?: string; country?: string; postalCode?: string; userId?: string }
export interface OrderItemDTO { orderId?: number; skuDTO?: SKUDTO; quantity?: number; price?: number; isReturned?: boolean }
export interface OrderDTO { orderId?: number; orderDate?: string; orderStatus?: string; totalPrice?: number; orderItems?: OrderItemDTO[]; shippingAddress?: AddressDTO }
export interface GuestOrderDTO { orderId?: number; orderDate?: string; orderStatus?: string; totalPrice?: number; orderItemDTOs?: OrderItemDTO[]; shippingAddress?: AddressDTO }
export interface PaymentDTO { paymentMethod?: string; transactionId?: string; amount?: number }
export interface StockUpdateDTO { stock: number }
