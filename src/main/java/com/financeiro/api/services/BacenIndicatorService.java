package com.financeiro.api.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class BacenIndicatorService {

    private final Map<String, BigDecimal> cachedIndicators = new HashMap<>();

    @Autowired
    private RestTemplate restTemplate;

    public BacenIndicatorService() {
        cachedIndicators.put("CDI", new BigDecimal("10.65"));
        cachedIndicators.put("SELIC", new BigDecimal("10.75"));
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
                cachedIndicators.put("SELIC_DIARIA", new BigDecimal(selicData[0].valor));
            }
        } catch (Exception e) {
            System.err.println("Falha ao buscar SELIC.");
        }

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        try {
            String cdiUrl = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.12/dados/ultimos/1?formato=json";
            BacenResponse[] cdiData = restTemplate.getForObject(cdiUrl, BacenResponse[].class);
            if (cdiData != null && cdiData.length > 0) {
                cachedIndicators.put("CDI_DIARIA", new BigDecimal(cdiData[0].valor));
            }
        } catch (Exception e) {
            System.err.println("Falha ao buscar CDI.");
        }
    }

    private static class BacenResponse {
        public String data;
        public String valor;
    }
}