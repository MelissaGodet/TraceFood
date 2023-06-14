package hr.algebra.tracefood.webapp.controller;

import hr.algebra.tracefood.webapp.model.*;
import hr.algebra.tracefood.webapp.service.*;
import io.prometheus.client.Gauge;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class SignController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public ProcessorService processorService;

    @Autowired
    public HoReCaService hoReCaService;

    @Autowired
    public ProducerService producerService;

    @Autowired
    public SellerService sellerService;

    //metric
    Gauge isLogged = Gauge.build()
            .name("is_logged")
            .help("Status of connexion")
            .register();

    @GetMapping("/signIn")
    public String signIn(Model model) {
        return "signIn";
    }

    @GetMapping("/chooseUserType")
    public String chooseUserType(Model model) {
        return "chooseUserType";
    }

    @GetMapping("/signUpHoReCa")
    public String signUpHoReCa() {
        return "signUpHoReCa";
    }

    @GetMapping("/signUpProducer")
    public String signUpProducer() {
        return "signUpProducer";
    }

    @GetMapping("/signUpProcessor")
    public String signUpProcessor() {
        return "signUpProcessor";
    }

    @GetMapping("/signUpSeller")
    public String signUpSeller() {
        return "signUpSeller";
    }

    @PostMapping("/signUpSeller")
    public String SignUpSellerForm(@RequestParam("companyName") String companyName,
                                   @RequestParam("companyAddress") String companyAddress,
                                   @RequestParam("type") String type,
                                   @RequestParam("emailAddress") String emailAddress,
                                   @RequestParam("password") String password, Model model) {

        List<Seller> sellers = sellerService.getAll();
        boolean signUpSuccessful = true;

        for (Seller seller : sellers) {
            if (seller.getUser().getEmailAddress().equals(emailAddress)) {
                signUpSuccessful = false;
            }
        }

        if (signUpSuccessful) {
            HttpSession session = request.getSession();
            Seller newSeller = sellerService.createSellerOptimized(emailAddress, password, companyName, companyAddress, SellerType.valueOf(type));
            session.setAttribute("user", newSeller);
            session.setAttribute("userType", UserType.SELLER.toString());
            isLogged.set(1);
            return "redirect:/userHomePage";

        } else {
            model.addAttribute("error", true);
            return "signUpSeller";
        }

    }

    @PostMapping("/signUpProcessor")
    public String SignUpProcessorForm(@RequestParam("companyName") String companyName,
                                      @RequestParam("companyAddress") String companyAddress,
                                      @RequestParam("type") String type,
                                      @RequestParam("emailAddress") String emailAddress,
                                      @RequestParam("password") String password, Model model) {

        List<Processor> processors = processorService.getAll();
        boolean signUpSuccessful = true;

        for (Processor processor : processors) {
            if (processor.getUser().getEmailAddress().equals(emailAddress)) {
                signUpSuccessful = false;
            }
        }

        if (signUpSuccessful) {
            HttpSession session = request.getSession();
            Processor newProcessor = processorService.createProcessorOptimized(emailAddress, password, companyName, companyAddress, ProcessorType.valueOf(type));
            session.setAttribute("user", newProcessor);
            session.setAttribute("userType", UserType.PROCESSOR.toString());
            isLogged.set(1);
            return "redirect:/userHomePage";

        } else {
            model.addAttribute("error", true);

            return "signUpProcessor";
        }

    }

    @PostMapping("/signUpProducer")
    public String SignUpProducerForm(@RequestParam("companyName") String companyName,
                                     @RequestParam("companyAddress") String companyAddress,
                                     @RequestParam("type") String type,
                                     @RequestParam("emailAddress") String emailAddress,
                                     @RequestParam("password") String password, Model model) {

        List<Producer> producers = producerService.getAll();
        boolean signUpSuccessful = true;

        for (Producer producer : producers) {
            if (producer.getUser().getEmailAddress().equals(emailAddress)) {
                signUpSuccessful = false;
            }
        }

        if (signUpSuccessful) {
            HttpSession session = request.getSession();
            Producer newProducer = producerService.createProducerOptimized(emailAddress, password, companyName, companyAddress, ProducerType.valueOf(type));
            session.setAttribute("userType", UserType.PRODUCER.toString());
            session.setAttribute("user", newProducer);
            isLogged.set(1);
            return "redirect:/userHomePage";

        } else {
            model.addAttribute("error", true);

            return "signUpProducer";
        }

    }

    @PostMapping("/signUpHoReCa")
    public String SignUpHoReCaForm(@RequestParam("companyName") String companyName,
                                   @RequestParam("emailAddress") String emailAddress,
                                   @RequestParam("companyAddress") String companyAddress,
                                   @RequestParam("password") String password,
                                   @RequestParam("type") String type,
                                   Model model) {

        List<HoReCa> hoReCas = hoReCaService.getAll();
        boolean signUpSuccessful = true;

        for (HoReCa hoReCa : hoReCas) {
            if (hoReCa.getUser().getEmailAddress().equals(emailAddress)) {
                signUpSuccessful = false;
            }
        }

        if (signUpSuccessful) {
            HttpSession session = request.getSession();
            HoReCa newHoReCa = hoReCaService.createHoReCaOptimized(emailAddress, password, companyName, companyAddress, HoReCaType.valueOf(type));
            session.setAttribute("userType", UserType.HORECA.toString());
            session.setAttribute("user", newHoReCa);
            isLogged.set(1);
            return "redirect:/userHomePage";

        } else {
            model.addAttribute("error", true);

            return "signUpHoReCa";
        }

    }


    @PostMapping("/signIn")
    public String SignIn(@RequestParam("emailAddress") String emailAddress, @RequestParam("password") String password, @RequestParam("type") String type, Model model) {

        boolean connexionSuccessful = false;
        HttpSession session = request.getSession();
        String hashPassword = DigestUtils.sha256Hex(password);

        switch (type) {
            case "Seller":
                SellerService sellerService = new SellerService();
                List<Seller> sellers = sellerService.getAll();
                Seller rightSeller = null;
                for (Seller seller : sellers) {
                    User user = seller.getUser();
                    if (user.getEmailAddress().equals(emailAddress) && user.getPassword().equals(hashPassword)) {
                        connexionSuccessful = true;
                        rightSeller = seller;
                    }
                }
                if (connexionSuccessful) {
                    session.setAttribute("userType", type);
                    session.setAttribute("user", rightSeller);
                    isLogged.set(1);
                    return "redirect:/userHomePage";
                } else {
                    model.addAttribute("error", true);
                    return "signIn";
                }
            case "Processor":
                ProcessorService processorService = new ProcessorService();
                List<Processor> processors = processorService.getAll();
                Processor rightProcessor = null;
                for (Processor processor : processors) {
                    User user = processor.getUser();
                    if (user.getEmailAddress().equals(emailAddress) && user.getPassword().equals(hashPassword)) {
                        connexionSuccessful = true;
                        rightProcessor = processor;
                    }
                }
                if (connexionSuccessful) {
                    session.setAttribute("user", rightProcessor);
                    session.setAttribute("userType", type);
                    isLogged.set(1);
                    return "redirect:/userHomePage";
                } else {
                    model.addAttribute("error", true);
                    return "signIn";
                }
            case "Producer":
                ProducerService producerService = new ProducerService();
                List<Producer> producers = producerService.getAll();
                Producer rightProducer = null;
                for (Producer producer : producers) {
                    User user = producer.getUser();
                    if (user.getEmailAddress().equals(emailAddress) && user.getPassword().equals(hashPassword)) {
                        connexionSuccessful = true;
                        rightProducer = producer;
                    }
                }
                if (connexionSuccessful) {
                    session.setAttribute("userType", type);
                    session.setAttribute("user", rightProducer);
                    isLogged.set(1);
                    return "redirect:/userHomePage";
                } else {
                    model.addAttribute("error", true);
                    return "signIn";
                }
            case "HoReCa":
                HoReCaService hoReCaService = new HoReCaService();
                List<HoReCa> hoReCas = hoReCaService.getAll();
                HoReCa rightHoReCa = null;
                for (HoReCa hoReCa : hoReCas) {
                    User user = hoReCa.getUser();
                    if (user.getEmailAddress().equals(emailAddress) && user.getPassword().equals(hashPassword)) {
                        connexionSuccessful = true;
                        rightHoReCa = hoReCa;
                    }
                }
                if (connexionSuccessful) {
                    session.setAttribute("userType", type);
                    session.setAttribute("user", rightHoReCa);
                    isLogged.set(1);
                    return "redirect:/userHomePage";
                } else {
                    model.addAttribute("error", true);
                    return "signIn";
                }
            default:
                System.out.println("User type not recognized.");
                model.addAttribute("error", true);
                return "signIn";
        }
    }

    @PostMapping("/signOut")
    public String SignOut(Model model) {
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        session.removeAttribute("userType");
        isLogged.set(0);
        return "redirect:/homePage";
    }

}
