package com.example.ordermodule.enums;

public enum PaymentStatus {
    PAID, // đã thanh toán
    UNPAID, // chưa thanh toán
    REFUND, // yêu cầu trả lại tiền
    REFUNDED, // đã trả lại tiền
    FAIL // không đủ tiền
}
