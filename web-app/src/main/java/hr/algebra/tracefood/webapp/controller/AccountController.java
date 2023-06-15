package hr.algebra.tracefood.webapp.controller;

import hr.algebra.tracefood.webapp.model.*;
import hr.algebra.tracefood.webapp.service.*;
import io.prometheus.client.Summary;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static hr.algebra.tracefood.webapp.model.OperationDisplay.sortOperations;

@Controller
public class AccountController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductionService productionService;

    @Autowired
    private TransportService transportService;

    @Autowired
    private ProcessingService processingService;

    //metric
    private static final Summary getHistoryLatency = Summary.build()
            .name("history_latency_counter")
            .help("Calculation latency of the History in seconds")
            .register();


    //functional programming
    @GetMapping("/accountInformation")
    public String accountInformation(Model model) {

        HttpSession session = request.getSession();
        Object userObject = session.getAttribute("user");
        User user = new User();
        String userType = (String) session.getAttribute("userType");

        BiConsumer<User, Model> attributeSetter = (usr, mdl) -> {
            mdl.addAttribute("companyName", usr.getCompanyName());
            mdl.addAttribute("companyAddress", usr.getEmailAddress());
        };

        if (userType != null) {
            switch (userType) {
                case "Seller":
                    Seller seller = (Seller) userObject;
                    user = seller.getUser();
                    attributeSetter.accept(user, model);
                    model.addAttribute("passwordLength", user.getPassword().length());
                    break;
                case "Producer":
                    Producer producer = (Producer) userObject;
                    user = producer.getUser();
                    attributeSetter.accept(user, model);
                    break;
                case "HoReCa":
                    HoReCa hoReCa = (HoReCa) userObject;
                    user = hoReCa.getUser();
                    attributeSetter.accept(user, model);
                    break;
                default:
                    Processor processor = (Processor) userObject;
                    user = processor.getUser();
                    attributeSetter.accept(user, model);
                    break;
            }
        }

        //model.addAttribute("allCertifications", new CertificationService().getAll());

        return "accountInformation";
    }


    //functionnal programming

    @GetMapping("/history")

    public String history(Model model) {

        Summary.Timer timer = getHistoryLatency.startTimer();
        HttpSession session = request.getSession();
        Object userObject = session.getAttribute("user");
        User user = new User();
        String userType = (String) session.getAttribute("userType");

        TransportService transportService = new TransportService();
        ProcessingService processingService = new ProcessingService();
        ProductionService productionService = new ProductionService();

        List<OperationDisplay> operations = new ArrayList<>();
        List<Transport> transports = new ArrayList<>();

        if (userType != null) {
            switch (userType) {
                case "Processor":
                    Processor processor = (Processor) userObject;
                    List<Processing> processes = processingService.getByProcessorId(processor.getId());
                    transports = transportService.getBySenderId(processor.getUser().getId());
                    operations.addAll(map(processes, processingService::toOperationDisplay));
                    break;
                case "Producer":
                    Producer producer = (Producer) userObject;
                    List<Production> productions = productionService.getAllByProducerId(producer.getId());
                    transports = transportService.getBySenderId(producer.getUser().getId());
                    operations.addAll(map(productions, productionService::toOperationDisplay));
                    break;
                case "Seller":
                    Seller seller = (Seller) userObject;
                    transports = transportService.getBySenderId(seller.getUser().getId());
                    break;
                default:
                    HoReCa hoReCa = (HoReCa) userObject;
                    transports = transportService.getBySenderId(hoReCa.getUser().getId());
                    break;
            }
        }

        operations.addAll(map(transports, transportService::toOperationDisplay));

        sortOperations(operations);

        model.addAttribute("operations", operations);

        double latency = timer.observeDuration();
        System.out.println("Latency to get the history of the account: " + latency + " seconds");

        return "history";
    }

    private <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        return list.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    @PostMapping("/addNewCertification")
    public String addNewCertification(Model model, @RequestParam("newCertification") CertificationType newCertification) {
        /*HttpSession session = request.getSession();
        List<CertificationType> giveableCertifications = session.getAttribute("user").getGiveableCertification();
        giveableCertifications.add(newCertification);
        session.getAttribute("user").setGiveableCertification(giveableCertifications);*/
        return "redirect:/accountInformation";
    }

}
