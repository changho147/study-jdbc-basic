package me.study.jdbc.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CheckedTest {

    @Test
    public void checkedCatch() {
        Service service = new Service();

        service.callCatch();
    }

    @Test
    public void checkedThrow() {
        Service service = new Service();

        assertThatThrownBy(service::callThrow).isInstanceOf(MyCheckedException.class);
    }

    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();

        public void callCatch() {
            try {
                repository.call();
            } catch (MyCheckedException e) {
                log.info("ex catch {}", e.getMessage(), e);
            }
        }

        public void callThrow() throws MyCheckedException {
            repository.call();
        }
    }

    static class Repository {
        public void call() throws MyCheckedException {
            throw new MyCheckedException("ex");
        }
    }
}
