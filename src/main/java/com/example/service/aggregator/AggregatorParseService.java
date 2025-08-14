package com.example.service.aggregator;

import com.example.dto.RecruiterDto;
import com.example.dto.agregator_dto.AggregatorResponseDto;
import com.example.dto.agregator_dto.FilterRequestDto;
import com.example.dto.agregator_dto.ResponseParseArrayDto;
import com.example.enums.SourceType;
import com.example.model.Source;
import com.example.util.WebElementsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorParseService {
    private final WebElementsUtil webElementsUtil;
    private final Integer maxRetries = 3;

    public List<ResponseParseArrayDto> parseWithFilter(FilterRequestDto filterRequestDto) {
        List<Source> sourceArray = new ArrayList<>();  // Этот массив мы должны получить из базы
        Source geekjob = new Source("geekjob",
                "https://geekjob.ru/vacancies",
                SourceType.SITE_HTML,
                "input[name='queryinput']",
                "#serplist > li.collection-item.avatar",
                "p.truncate.vacancy-name",
                "p.truncate.vacancy-name > a.title"
        );
        sourceArray.add(geekjob);

        List<ResponseParseArrayDto> items = new ArrayList<>();
        for (Source source : sourceArray) {
            List<AggregatorResponseDto> parseItems = parse(source, filterRequestDto.searchField());
            ResponseParseArrayDto responseParseArrayDto = new ResponseParseArrayDto(source.getName(), parseItems);
            items.add(responseParseArrayDto);
        }
        return items;
    }

    public List<AggregatorResponseDto> parse(Source source, String searchText) {
        try {
            webElementsUtil.getDriver().get(source.getBaseUrl());

            inputSearchField(source.getSearchField(), searchText);

            List<AggregatorResponseDto> items = new ArrayList<>();

            By by = By.cssSelector(source.getArrayArticlesField());
            List<WebElement> articles = webElementsUtil.getWait().until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(by)
            );
            for (WebElement article : articles) {
                WebElement titleElement = retryFindElement(() ->
                        article.findElement(By.cssSelector(source.getVacancyNameField())), maxRetries
                );
                WebElement linkElement = retryFindElement(() ->
                        article.findElement(By.cssSelector(source.getVacancyUrlField())), maxRetries
                );
                String title = titleElement.getText();
                String url = linkElement.getAttribute("href");

                items.add(new AggregatorResponseDto(title, url, null, null, new RecruiterDto("", "", "", "")));
            }
            log.info("Parse items.size: {} for: {}", items.size(), source.getBaseUrl());
            return items;
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            throw new RuntimeException("Selenium error", e);
        }
    }

    private void inputSearchField(String selector, String searchText) {
        By locator = By.cssSelector(selector);
        WebElement webElement = webElementsUtil.putTextToInputField(locator, searchText);
        webElement.sendKeys(Keys.ENTER);
    }

    private WebElement retryFindElement(Supplier<WebElement> supplier, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                return supplier.get();
            } catch (StaleElementReferenceException e) {
                attempts++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw new RuntimeException("Не удалось получить элемент после " + maxRetries + " попыток");
    }

}
