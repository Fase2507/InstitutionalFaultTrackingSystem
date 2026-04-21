package tr.duzce.edu.mf.bm.KurumArizaTakip.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(HttpServletRequest request, Exception ex) {
        log.error("Unhandled exception method={} path={} msg={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("message", "Beklenmeyen bir hata oluştu.");
        return mv;
    }
}
