package exchange_application.service;

import exchange_application.dao.ExchangeDAO;
import exchange_application.model.Currency;
import exchange_application.model.CurrencyEnum;
import exchange_application.model.ExchangeRate;
import exchange_application.model.User;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class ExchangeService {

    private final ExchangeDAO dao;
    final Lock lock = new ReentrantLock();

    private static final Logger logger = Logger.getLogger(ExchangeService.class.getName());


    public ExchangeService(ExchangeDAO dao) {
        this.dao = dao;
    }

    public void createSampleData(User user) {
        dao.createSampleData(user);
    }


    public void exchange(CurrencyEnum source, CurrencyEnum target) {
        logger.info(Thread.currentThread().getName() + " starting..");
        try {
            lock.lock();
            logger.info(Thread.currentThread().getName() + " locked..");
            User user = dao.read();
            Currency currencySource = user.getCurrencies().stream().filter(c -> c.getName() == source).findFirst().orElseThrow();
            Currency currencyTarget = user.getCurrencies().stream().filter(c -> c.getName() == target).findFirst().orElseThrow();

            logger.info(Thread.currentThread().getName() + " values before calc, " + source + " " + currencySource.getValue() + " -> " + target + " " + currencyTarget.getValue());

            BigDecimal exchangeRate = ExchangeRate.getExchangeRate(currencySource.getName(), currencyTarget.getName());
            BigDecimal exchange = currencySource.getValue().multiply(exchangeRate);

            BigDecimal newSource = BigDecimal.ZERO;
            BigDecimal newTarget = currencyTarget.getValue().add(exchange);

            logger.info(Thread.currentThread().getName() + " values before calc, " + source + " " + newSource + " -> " + target + " " + newTarget + " with exchange rate " + exchangeRate);

            currencySource.setValue(newSource);
            currencyTarget.setValue(newTarget);

            dao.write(user);
        } finally {
            logger.info(Thread.currentThread().getName() + " unlocked");
            lock.unlock();
        }
        System.out.println();
    }


}
