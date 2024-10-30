package exchange_application;

import exchange_application.dao.ExchangeDAO;
import exchange_application.model.Currency;
import exchange_application.model.CurrencyEnum;
import exchange_application.model.User;
import exchange_application.service.ExchangeService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.*;

public class Main {


    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        ExchangeService service = new ExchangeService(new ExchangeDAO());

        Currency eur = new Currency(CurrencyEnum.EUR, BigDecimal.valueOf(100));
        Currency usd = new Currency(CurrencyEnum.USD, BigDecimal.valueOf(100));
        User user = new User("Alex", Arrays.asList(eur, usd));
        service.createSampleData(user);


        ExecutorService es = Executors.newFixedThreadPool(2);
        Future future1 = es.submit(() -> service.exchange(CurrencyEnum.EUR, CurrencyEnum.USD));
        Future future2 = es.submit(() -> service.exchange(CurrencyEnum.USD, CurrencyEnum.EUR));

        future1.get(10, TimeUnit.SECONDS);
        future2.get(10, TimeUnit.SECONDS);
    }


}
