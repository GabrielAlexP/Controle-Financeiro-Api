package com.financeiro.api.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class BacenIndicatorService {

    private final Map<String, BigDecimal> cachedIndicators = new HashMap<>();

    @Autowired
    private RestTemplate restTemplate;

    public BacenIndicatorService() {

        BigDecimal defaultSelic = new BigDecimal("14.90");
        BigDecimal defaultCdi = new BigDecimal("14.90");
        
        cachedIndicators.put("SELIC", defaultSelic);
        cachedIndicators.put("SELIC_DIARIA", calculateDailyRate(defaultSelic));
        cachedIndicators.put("CDI", defaultCdi);
        cachedIndicators.put("CDI_DIARIA", calculateDailyRate(defaultCdi));
    }

    public Map<String, BigDecimal> getIndicators() {
        return cachedIndicators;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 8 * * *")
    public void updateIndicators() {
        try {
            String selicUrl = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.11/dados/ultimos/1?formato=json";
            BacenResponse[] selicData = restTemplate.getForObject(selicUrl, BacenResponse[].class);
            if (selicData != null && selicData.length > 0) {
                BigDecimal selicDiaria = new BigDecimal(selicData[0].valor);
                cachedIndicators.put("SELIC_DIARIA", selicDiaria);
                cachedIndicators.put("SELIC", calculateAnnualRate(selicDiaria));
            }
        } catch (Exception e) {
            System.err.println("Falha ao buscar SELIC. Mantendo cache anterior.");
        }

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        try {
            String cdiUrl = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.12/dados/ultimos/1?formato=json";
            BacenResponse[] cdiData = restTemplate.getForObject(cdiUrl, BacenResponse[].class);
            if (cdiData != null && cdiData.length > 0) {
                BigDecimal cdiDiaria = new BigDecimal(cdiData[0].valor);
                cachedIndicators.put("CDI_DIARIA", cdiDiaria);
                cachedIndicators.put("CDI", calculateAnnualRate(cdiDiaria));
            }
        } catch (Exception e) {
            System.err.println("Falha ao buscar CDI. Mantendo cache anterior.");
        }
    }

    private BigDecimal calculateDailyRate(BigDecimal annualRate) {
        double annual = annualRate.doubleValue() / 100.0;
        double daily = (Math.pow(1.0 + annual, 1.0 / 252.0) - 1.0) * 100.0;
        return new BigDecimal(daily).setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAnnualRate(BigDecimal dailyRate) {
        double daily = dailyRate.doubleValue() / 100.0;
        double annual = (Math.pow(1.0 + daily, 252.0) - 1.0) * 100.0;
        return new BigDecimal(annual).setScale(2, RoundingMode.HALF_UP);
    }

    private static class BacenResponse {
        public String data;
        public String valor;
    }
}