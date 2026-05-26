package selenide.helpers;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Allure;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class SelenideSoftAssert {

    private final List<String> errors = new ArrayList<>();
    private boolean hasErrors = false;

    public void executeStep(Runnable step, String stepName) {
        try {
            step.run();
            log.info("✅ Шаг '{}' выполнен успешно", stepName);
        } catch (AssertionError e) {
            handleError(stepName, "Assertion", e);
        } catch (Exception e) {
            handleError(stepName, "Exception", e);
        }
    }

    private void handleError(String stepName, String errorType, Throwable e) {
        String error = String.format("❌ Шаг '%s' провален (%s): %s", stepName, errorType, e.getMessage());
        errors.add(error);
        hasErrors = true;
        log.error(error);

        Allure.addAttachment("Ошибка в шаге: " + stepName, e.getMessage());
        takeScreenshot(stepName);
    }

    private void takeScreenshot(String name) {
        try {
            // В Selenide скриншот сохраняется в файл, а не возвращает байты
            Selenide.screenshot(name);
            log.debug("Скриншот сохранен: {}", name);
        } catch (Exception e) {
            log.warn("Не удалось сделать скриншот: {}", e.getMessage());
        }
    }

    public void failIfErrors() {
        if (hasErrors) {
            String allErrors = String.join("\n", errors);
            log.error("Тест завершен с {} ошибками:\n{}", errors.size(), allErrors);
            Allure.addAttachment("Все ошибки теста", "text/plain", allErrors);
            throw new AssertionError(String.format("Тест провален с %d ошибками:\n%s", errors.size(), allErrors));
        }
        log.info("✅ Все шаги выполнены успешно");
        Allure.addAttachment("Результат теста", "text/plain", "✅ УСПЕШНО");
    }

    public void assertAll() {
        failIfErrors();
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void clearErrors() {
        errors.clear();
        hasErrors = false;
    }


}
