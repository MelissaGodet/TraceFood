package hr.algebra.tracefood.webapp.controller;

import hr.algebra.tracefood.webapp.model.*;
import hr.algebra.tracefood.webapp.service.*;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class OperationController {

    //metric
    private static final Counter operationsCounter = Counter.build().name("operation_counter")
            .help("Count the number of operation")
            .register();

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/addAnOperation")
    public String addAnOperation() {
        return "addAnOperation";
    }

    @GetMapping("/addAProduction")
    public String addAProduction() {
        return "addAProduction";
    }

    @GetMapping("/addAProcessing")
    public String addAProcessing() {
        return "addAProcessing";
    }

    @GetMapping("/addATransport")
    public String addATransport() {
        return "addATransport";
    }

    @PostMapping("/addAProduction")

    public String addANewProduction(@RequestParam("productName") String productName,
                                    @RequestParam("description") String description,
                                    @RequestParam("dateOfCreation") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate productionDate,
                                    @RequestParam("productType") String productType,
                                    @RequestParam("isFinishedProduct") boolean isFinishedProduct) {
        HttpSession session = request.getSession();
        Producer producer = (Producer) session.getAttribute("user");
        ProductionService productionService = new ProductionService();
        operationsCounter.inc();
        productionService.createProductionOptimized(description, producer, productionDate, productName, ProductType.valueOf(productType), isFinishedProduct);

        return "redirect:/userHomePage";
    }

    @PostMapping("/addAProcessing")
    public String addANewProcessing(
                                    @RequestParam("OriginProductID") Long OriginProductID,
                                    @RequestParam("newProductName") String newProductName,
                                    @RequestParam("description") String description,
                                    @RequestParam("dateOfProcessing") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate processingDate,
                                    @RequestParam("productType") String newProductType,
                                    @RequestParam("isFinishedProduct") boolean isNewFinishedProduct) {
        operationsCounter.inc();
        HttpSession session = request.getSession();
        Processor processor = (Processor) session.getAttribute("user");
        ProcessingService processingService = new ProcessingService();
        ProductService productService = new ProductService();
        Product originProduct = productService.getById(OriginProductID);
        processingService.createProcessingOptimized(description, originProduct, newProductName, ProductType.valueOf(newProductType), processor, processingDate, isNewFinishedProduct);
        return "redirect:/userHomePage";
    }

    @PostMapping("/addATransport")
    public String addANewTransport(
            @RequestParam("productID") Long productID,
            @RequestParam("description") String description,
            @RequestParam("departureDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate departureDate,
            @RequestParam("arrivalDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate arrivalDate,
            @RequestParam("receiverID") Long receiverID) {

        operationsCounter.inc();
        HttpSession session = request.getSession();
        Object userObject = session.getAttribute("user");
        User sender = new User();
        String userType = (String) session.getAttribute("userType");
        if (userType != null) {
            switch (userType) {
                case "Seller":
                    Seller seller = (Seller) session.getAttribute("user");
                    sender = seller.getUser();
                    break;
                case "Producer":
                    Producer producer = (Producer)session.getAttribute("user");
                    sender = producer.getUser();
                    break;
                case "Processor":
                    Processor processor = (Processor) session.getAttribute("user");
                    sender = processor.getUser();
                    break;
                case "HoReCa":
                    HoReCa hoReCa = (HoReCa) session.getAttribute("user");
                    sender = hoReCa.getUser();
                    break;
                default:
                    System.out.println("The user is not recognized");
                    break;
            }
        }
        UserService userService = new UserService();
        User receiver = userService.getById(receiverID);
        TransportService transportService = new TransportService();
        ProductService productService = new ProductService();
        Product product = productService.getById(productID);
        transportService.createTransportOptimized(description, product, sender, receiver, departureDate, arrivalDate);
        return "redirect:/userHomePage";
    }

}
