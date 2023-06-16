package hr.algebra.tracefood.webapp;

import hr.algebra.tracefood.webapp.model.*;
import hr.algebra.tracefood.webapp.service.ProcessorService;
import hr.algebra.tracefood.webapp.service.ProducerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TracfoodTest {

    @Test
    public void testProcessorCreation() {
        ProcessorService processorService = new ProcessorService();
        Processor expectedProcessor = processorService.createProcessorOptimized("emailAddress", "password", "companyName", "companyAddress", ProcessorType.valueOf("FLOURMILLER"));
        Processor actualProcessor = processorService.getById(expectedProcessor.getId());
        assertEquals(expectedProcessor.getUser().getCompanyName(), actualProcessor.getUser().getCompanyName());
        assertEquals(expectedProcessor.getUser().getEmailAddress(), actualProcessor.getUser().getEmailAddress());
        assertEquals(expectedProcessor.getUser().getAddress(), actualProcessor.getUser().getAddress());
    }

    @Test
    public void testProducerDeletion() {
        ProducerService producerService = new ProducerService();
        Producer producer = producerService.createProducerOptimized("emailAddress", "password", "companyName", "companyAddress", ProducerType.valueOf("BEEKEEPER"));
        producerService.deleteById(producer.getId());
        Assertions.assertThrows(HttpClientErrorException.class, () -> {producerService.getById(producer.getId());});
    }

    @Test
    public void testSort1Operations() {
        OperationType type = OperationType.PRODUCTION;
        Map<String, String> attributs = new HashMap<>();
        attributs.put("attribut1", "valeur1");
        attributs.put("attribut2", "valeur2");
        List<OperationDisplay> operations = new ArrayList<>();
        LocalDate date = LocalDate.of(2022, 6, 15);

        OperationDisplay.sortOperations(operations);
        assertEquals(date, new OperationDisplay(date, type, attributs).getDate());
    }

    @Test
    public void testAlreadySortOperations() {
        OperationType type = OperationType.PRODUCTION;
        Map<String, String> attributs = new HashMap<>();
        attributs.put("attribut1", "valeur1");
        attributs.put("attribut2", "valeur2");
        List<OperationDisplay> operations = new ArrayList<>();
        operations.add(new OperationDisplay(LocalDate.of(2022, 6, 15), type, attributs));
        operations.add(new OperationDisplay(LocalDate.of(2022, 6, 16), type, attributs));
        operations.add(new OperationDisplay(LocalDate.of(2022, 6, 17), type, attributs));

        OperationDisplay.sortOperations(operations);

        List<LocalDate> actualDates = operations.stream()
                .map(OperationDisplay::getDate)
                .collect(Collectors.toList());

        List<LocalDate> expectedDates = Arrays.asList(
                LocalDate.of(2022, 6, 15),
                LocalDate.of(2022, 6, 16),
                LocalDate.of(2022, 6, 17));

        assertEquals(expectedDates, actualDates);

    }

    @Test
    public void testSortSameOperations() {
        OperationType type = OperationType.PRODUCTION;
        Map<String, String> attributs = new HashMap<>();
        attributs.put("attribut1", "valeur1");
        attributs.put("attribut2", "valeur2");
        List<OperationDisplay> operations = new ArrayList<>();
        LocalDate date = LocalDate.of(2022, 6, 15);

        operations.add(new OperationDisplay(date, type, attributs));
        operations.add(new OperationDisplay(date, type, attributs));
        operations.add(new OperationDisplay(date, type, attributs));

        OperationDisplay.sortOperations(operations);

        List<LocalDate> actualDates = operations.stream()
                .map(OperationDisplay::getDate)
                .collect(Collectors.toList());

        List<LocalDate> expectedDates = Arrays.asList(date, date, date);
        assertEquals(expectedDates, actualDates);
    }

    @Test
    public void testSort3Operations() {
        LocalDate date1 = LocalDate.of(2022, 6, 15);
        LocalDate date2 = LocalDate.of(2022, 6, 16);
        LocalDate date3 = LocalDate.of(2022, 6, 17);
        OperationType type = OperationType.PRODUCTION;
        Map<String, String> attributs = new HashMap<>();
        attributs.put("attribut1", "valeur1");
        attributs.put("attribut2", "valeur2");
        List<OperationDisplay> operations = new ArrayList<>();
        operations.add(new OperationDisplay(date1, type, attributs ));
        operations.add(new OperationDisplay(date3, type, attributs ));
        operations.add(new OperationDisplay(date2, type, attributs ));

        OperationDisplay.sortOperations(operations);
        List<LocalDate> actualDates= new ArrayList<LocalDate>();
        for (OperationDisplay operation : operations){
            actualDates.add(operation.getDate());
        }

        List<LocalDate> expectedDates= new ArrayList<LocalDate>();
        expectedDates.add(date1);
        expectedDates.add(date2);
        expectedDates.add(date3);

        assertEquals(expectedDates, actualDates);
    }


}