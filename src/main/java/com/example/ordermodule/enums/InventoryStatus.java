package com.example.ordermodule.enums;

public enum InventoryStatus {
    DONE, // trừ kho thành công
    RETURN, // yêu cầu trả lại hàng
    RETURNED, // đã trả lại hàng
    PENDING, // chờ xử ý
    OUT_OF_STOCK // hết hàng
}
