package com.cinebee.util;

import com.cinebee.exception.ApiException;
import com.cinebee.exception.ErrorCode;

import java.util.Optional;
import java.util.function.Supplier;

public class ServiceUtils {

    public static <T> T findObjectOrThrow(Supplier<Optional<T>> supplier, ErrorCode errorCode) {
        return supplier.get().orElseThrow(() -> new ApiException(errorCode));
    }
}
