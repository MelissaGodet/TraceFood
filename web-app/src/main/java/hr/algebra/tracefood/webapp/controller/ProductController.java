package hr.algebra.tracefood.webapp.controller;

import hr.algebra.tracefood.webapp.model.*;
import hr.algebra.tracefood.webapp.service.*;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static hr.algebra.tracefood.webapp.model.OperationDisplay.sortOperations;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductionService productionService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private ProcessingService processingService;

    private static final Summary getProductLatency = Summary.build()
            .name("latency_counter")
            .help("Calculation latency of functions that give the info about a product in seconds")
            .register();


    @GetMapping("/informationAboutAProduct")
    public String informationAboutAProduct() {
        return "informationAboutAProduct";
    }

    @GetMapping("/userInformationAboutAProduct")
    public String userInformationAboutAProduct() {
        return "userInformationAboutAProduct";
    }


    @GetMapping("/searchForAProduct")
    public String searchForAProduct() {
        return "searchForAProduct";
    }

    @GetMapping("/userSearchForAProduct")
    public String userSearchForAProduct() {
        return "userSearchForAProduct";
    }

    @PostMapping("/informationAboutAProduct")
    public String searchForAProduct(Model model, @RequestParam("productId") Long productId) {
        Summary.Timer timer = getProductLatency.startTimer();
        List<OperationDisplay> operations = getOperations(productId);
        model.addAttribute("operations", operations);
        double latency = timer.observeDuration();
        System.out.println("Latency to get the information about the product : " + latency + " seconds");
        return "informationAboutAProduct";
    }

    @PostMapping("/userInformationAboutAProduct")
    public String userSearchForAProduct(Model model, @RequestParam("productId") Long productId) {
        Summary.Timer timer = getProductLatency.startTimer();
        List<OperationDisplay> operations = getOperations(productId);
        model.addAttribute("operations", operations);
        double latency = timer.observeDuration();
        System.out.println("Latency to get the information about the product : " + latency + " seconds");
        return "userInformationAboutAProduct";
    }

    private List<OperationDisplay> getOperations( Long productId) {
        Product product = productService.getById(productId);
        List<OperationDisplay> operations = new ArrayList<>();

        List<Production> productions = productionService.getByCreatedProductId(productId);
        List<Processing> processes = processingService.getAllByOriginProductId(productId);
        List<Transport> transports = transportService.getAllByProductId(productId);

        operations.addAll(addProductOperations(processes, transports, productions));


        while(product.getParent() != null){
            product = product.getParent();
            processes = processingService.getAllByOriginProductId(product.getId());
            transports = transportService.getAllByProductId(product.getId());
            productions = productionService.getByCreatedProductId(productId);
            operations.addAll(addProductOperations(processes, transports, productions));
        }

        sortOperations(operations);
        return operations;
    }

    private List<OperationDisplay> addProductOperations(List<Processing> processes, List<Transport> transports, List<Production> productions) {
        List<OperationDisplay> operations = new ArrayList<>();

        for (Processing processing : processes) {
            operations.add(processingService.toOperationDisplay(processing));
        }

        for (Transport transport : transports) {
            operations.add(transportService.toOperationDisplay(transport));
        }

        for (Production production : productions) {
            operations.add(productionService.toOperationDisplay(production));
        }
        return operations;
    }


}
