package com.cinebee.service;

import com.cinebee.dto.request.MomoPaymentRequest;
import com.cinebee.dto.response.MomoResponse;

import java.util.Map;

public interface PaymentService {
    MomoResponse createMomoPayment(MomoPaymentRequest request);
    void handleMomoIpn(Map<String, Object> ipnData);
    void handleMomoReturn(String orderId, String resultCode, String message);
    boolean verifyMomoReturnSignature(Map<String, String> params);
}
