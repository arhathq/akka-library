package library.core.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.context.request.async.DeferredResult;
import library.core.ApplicationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Kuleshov
 */
public abstract class AbstractRestController {

    public static final long WEB_SERVICE_TIMEOUT = TimeUnit.SECONDS.toMillis(30);

    protected <T> DeferredResult<ResponseEntity<T>> objectResult(ListenableFuture<T> futureResult) {
        DeferredResult<ResponseEntity<T>> response = new DeferredResult<>(WEB_SERVICE_TIMEOUT, errorResult("Request timeout", HttpStatus.REQUEST_TIMEOUT));
        futureResult.addCallback(new SimpleCallback<>(response));
        return response;
    }

    protected <T> DeferredResult<ResponseEntity<Map<String, T>>> mapResult(String key, ListenableFuture<T> futureResult) {
        DeferredResult<ResponseEntity<Map<String, T>>> response = new DeferredResult<>(WEB_SERVICE_TIMEOUT, errorResult("Request timeout", HttpStatus.REQUEST_TIMEOUT));
        futureResult.addCallback(new MapCallback<>(key, response));
        return response;
    }

    protected DeferredResult<ResponseEntity<Map<String, Object>>> errorResult(String errorMessage, HttpStatus status) {
        DeferredResult<ResponseEntity<Map<String, Object>>> response = new DeferredResult<>();
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorcode", status.value());
        result.put("errordescription", errorMessage);
        result.put("stacktrace", errorMessage);

        response.setResult((new ResponseEntity<>(result, status)));
        return response;
    }

    protected DeferredResult<ResponseEntity<Map<String, Object>>> exceptionResult(Throwable t, HttpStatus status) {
        DeferredResult<ResponseEntity<Map<String, Object>>> response = new DeferredResult<>();
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("errorcode", status.value());
        result.put("errordescription", t.getMessage());

        StringWriter stacktrace = new StringWriter();
        t.printStackTrace(new PrintWriter(stacktrace));
        result.put("stacktrace", stacktrace.toString());

        response.setResult((new ResponseEntity<>(result, status)));
        return response;
    }

    private class SimpleCallback<T> implements ListenableFutureCallback<T> {

        protected DeferredResult<ResponseEntity<T>> deferredResult;

        private SimpleCallback(DeferredResult<ResponseEntity<T>> deferredResult) {
            this.deferredResult = deferredResult;
        }

        @Override
        public void onSuccess(T result) {
            deferredResult.setResult(new ResponseEntity<>(result, HttpStatus.OK));
        }

        @Override
        public void onFailure(Throwable t) {
            deferredResult.setErrorResult(exceptionResult(t, HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private class MapCallback<T> implements ListenableFutureCallback<T> {

        private String key;

        protected DeferredResult<ResponseEntity<Map<String, T>>> deferredResult;

        private MapCallback(String key, DeferredResult<ResponseEntity<Map<String, T>>> deferredResult) {
            if (null == key) {
                throw new IllegalArgumentException("Parameter key is null");
            }
            this.key = key;
            this.deferredResult = deferredResult;
        }

        @Override
        public void onSuccess(T result) {
            Map<String, T> m = new HashMap<>();
            m.putAll(Collections.singletonMap(key, result));
            deferredResult.setResult(new ResponseEntity<>(m, HttpStatus.OK));
        }

        @Override
        public void onFailure(Throwable t) {
            if (t instanceof ApplicationException) {
                deferredResult.setErrorResult(errorResult(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
            } else {
                deferredResult.setErrorResult(exceptionResult(t, HttpStatus.INTERNAL_SERVER_ERROR));
            }
        }
    }
}
