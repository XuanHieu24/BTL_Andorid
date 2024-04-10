package com.example.btl_andorid.CustomAdapter;

import com.sinhvien.orderdrinkapp.DTO.ThanhToanDTO;

public interface OnclickItem {
    void OnClickBack(ThanhToanDTO thanhToanDTO);
    void OnClickNext(ThanhToanDTO thanhToanDTO);
    void OnLongClick(ThanhToanDTO thanhToanDTO);
}
