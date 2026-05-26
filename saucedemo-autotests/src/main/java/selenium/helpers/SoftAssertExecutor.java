package selenium.helpers;

import io.qameta.allure.Allure;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class SoftAssertExecutor {

    private final List<String> errors = new ArrayList<>();
    private boolean hasErrors = false;
    private WebDriver driver;

    public SoftAssertExecutor() {
    }

    public SoftAssertExecutor(WebDriver driver) {
        this.driver = driver;
    }

//    /**
//     * Выполняет шаг с мягкой проверкой
//     */
//    public void executeStep(Runnable step, String stepName) {
//        try {
//            // Используем лямбду, которая соответствует Allure.ThrowableRunnableVoid
//            Allure.step(stepName, () -> {
//                step.run();
//                return null; // Возвращаем null для Void
//            });
//            log.info("✅ Шаг '{}' выполнен успешно", stepName);
//        } catch (AssertionError e) {
//            String error = String.format("❌ Шаг '%s' провален (Assertion): %s", stepName, e.getMessage());
//            errors.add(error);
//            hasErrors = true;
//            log.error(error);
//            Allure.addAttachment("Ошибка в шаге: " + stepName, e.getMessage());
//        } catch (Exception e) {
//            String error = String.format("❌ Шаг '%s' провален (Exception): %s", stepName, e.getMessage());
//            errors.add(error);
//            hasErrors = true;
//            log.error(error, e);
//            Allure.addAttachment("Ошибка в шаге: " + stepName, e.getMessage());
//        }
//    }

    /**
      * Выполняет шаг с мягкой проверкой
     */
    public void executeStep(Runnable step, String stepName) {
        try {
            Allure.step(stepName, () -> {
                step.run();
                return null;
            });
            log.info("✅ Шаг '{}' выполнен успешно", stepName);
        } catch (AssertionError e) {
            handleError(stepName, "Assertion", e);
        } catch (Exception e) {
            handleError(stepName, "Exception", e);
        }
    }

    /**
     * Обработка ошибки с созданием скриншота
     */
    private void handleError(String stepName, String errorType, Throwable e) {
        String error = String.format("❌ Шаг '%s' провален (%s): %s", stepName, errorType, e.getMessage());
        errors.add(error);
        hasErrors = true;
        log.error(error);

        // Добавляем текст ошибки
        Allure.addAttachment("Ошибка в шаге: " + stepName, "text/plain", e.getMessage());

        // Делаем скриншот
        takeScreenshot("Скриншот ошибки - " + stepName);
    }

    /**
     * Создает скриншот и прикрепляет к Allure
     */
    private void takeScreenshot(String name) {
        if (driver == null) {
            log.warn("WebDriver не установлен, скриншот не создан");
            return;
        }

        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
            log.debug("Скриншот создан: {}", name);
        } catch (Exception e) {
            log.error("Не удалось создать скриншот: {}", e.getMessage());
        }
    }

    /**
     * Выполняет шаг с мягкой проверкой и возвращает результат
     */
    public <T> T executeStepWithResult(StepWithResult<T> step, String stepName) {
        try {
            return Allure.step(stepName, () -> {
                try {
                    return step.execute();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (AssertionError e) {
            String error = String.format("❌ Шаг '%s' провален (Assertion): %s", stepName, e.getMessage());
            errors.add(error);
            hasErrors = true;
            log.error(error);
            Allure.addAttachment("Ошибка в шаге: " + stepName, e.getMessage());
            return null;
        } catch (Exception e) {
            String error = String.format("❌ Шаг '%s' провален (Exception): %s", stepName, e.getMessage());
            errors.add(error);
            hasErrors = true;
            log.error(error, e);
            Allure.addAttachment("Ошибка в шаге: " + stepName, e.getMessage());
            return null;
        }
    }

    /**
     * Проверяет наличие ошибок и падает, если они есть
     */
    public void failIfErrors() {
        if (hasErrors) {
            // Делаем финальный скриншот
            takeScreenshot("Финальный скриншот при падении теста");

            String allErrors = String.join("\n", errors);
            log.error("Тест завершен с {} ошибками:\n{}", errors.size(), allErrors);
            Allure.addAttachment("Все ошибки теста", "text/plain", allErrors);

            throw new AssertionError(String.format("Тест провален с %d ошибками:\n%s",
                    errors.size(), allErrors));
        }
        log.info("✅ Все шаги выполнены успешно");
        Allure.addAttachment("Результат теста", "text/plain", "✅ УСПЕШНО");
    }

    /**
     * Устанавливает WebDriver для создания скриншотов
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    @FunctionalInterface
    public interface StepWithResult<T> {
        T execute() throws Exception;
    }

    /**
     * Проверяет все шаги (синоним для failIfErrors)
     */
    public void assertAll() {
        failIfErrors();
    }

    /**
     * Проверяет, были ли ошибки (без бросания исключения)
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * Получить список ошибок
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Очистить ошибки
     */
    public void clearErrors() {
        errors.clear();
        hasErrors = false;
    }


}