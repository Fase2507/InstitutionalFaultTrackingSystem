package tr.duzce.edu.mf.bm.KurumArizaTakip.web.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String START_NANOS = RequestLoggingInterceptor.class.getName() + ".startNanos";
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "pass", "passwd",
            "code", "verificationCode",
            "token", "access_token", "refresh_token"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_NANOS, System.nanoTime());
        log.info("REQ_IN method={} path={} params={}", request.getMethod(), request.getRequestURI(), maskedParams(request));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) {
        String view = modelAndView != null ? modelAndView.getViewName() : null;
        if (view != null) {
            request.setAttribute("viewName", view);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) {
        long elapsedMs = elapsedMs(request);
        Object viewName = request.getAttribute("viewName");
        if (ex == null) {
            log.info("REQ_OUT status={} view={} elapsedMs={}", response.getStatus(), viewName, elapsedMs);
        } else {
            log.error("REQ_ERR status={} view={} elapsedMs={} ex={}", response.getStatus(), viewName, elapsedMs, ex);
        }
    }

    private static long elapsedMs(HttpServletRequest request) {
        Object v = request.getAttribute(START_NANOS);
        if (v instanceof Long start) {
            return (System.nanoTime() - start) / 1_000_000;
        }
        return -1;
    }

    private static Map<String, Object> maskedParams(HttpServletRequest request) {
        Map<String, Object> out = new LinkedHashMap<>();
        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> e : map.entrySet()) {
            String key = e.getKey();
            if (key == null) continue;

            if (isSensitive(key)) {
                out.put(key, "***");
                continue;
            }

            String[] values = e.getValue();
            if (values == null) {
                out.put(key, null);
            } else if (values.length == 1) {
                out.put(key, values[0]);
            } else {
                out.put(key, Arrays.asList(values));
            }
        }
        return out;
    }

    private static boolean isSensitive(String key) {
        String k = key.toLowerCase(Locale.ROOT);
        return SENSITIVE_KEYS.contains(k);
    }
}
