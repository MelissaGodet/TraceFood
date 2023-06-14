package hr.algebra.tracefood.webapp.controller;

import io.prometheus.client.Counter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorController {

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    //metric
    private static final Counter errorCounter = Counter.build().name("error_counter")
            .help("Count the number of error")
            .register();



    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        errorCounter.inc();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }
}
