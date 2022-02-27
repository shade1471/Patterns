package ru.netology.delivery.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryTest {
    DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
    DataGenerator.UserInfo validUserEn = DataGenerator.Registration.generateUser("en");

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
        $("[data-test-id=date] .input__control").sendKeys(Keys.CONTROL + "A");
        $("[data-test-id=date] .input__control").sendKeys(Keys.BACK_SPACE);
    }

    @Test
    void shouldOrderCardWithEmptyFieldByCity() {
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(3));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=city].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldOrderCardWithEmptyFieldByName() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(4));
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldOrderCardWithEmptyFieldByPhone() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(3));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(text("Поле обязательно для заполнения"));
    }

    @Test
    void shouldOrderCardWithoutCheckBox() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(3));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $(byText("Запланировать")).click();
        $(".checkbox.input_invalid").shouldBe(visible);
    }

    @Test
    void shouldOrderCardWithDateLessThreeDays() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").setValue(DataGenerator.generateDate(2));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=date] .input_invalid .input__sub")
                .shouldHave(text("Заказ на выбранную дату невозможен"));
    }

    @Test
    public void shouldSendFormWithDoubleSurname() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        String dateDelivery = DataGenerator.generateDate(3);
        $("[data-test-id=date] .input__control").setValue(dateDelivery);
        $("[data-test-id=name] .input__control").setValue(DataGenerator.generateDoubleSurname());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(text("Встреча успешно запланирована на " + dateDelivery), Duration.ofSeconds(15));
    }

    @Test
    public void shouldSendFormByNameOfCityWithDash() {
        $("[data-test-id=city] .input__control").setValue(DataGenerator.generateDoubleCity());
        String dateDelivery = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=date] .input__control").setValue(dateDelivery);
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(text("Встреча успешно запланирована на " + dateDelivery), Duration.ofSeconds(15));
    }

    @Test
    public void shouldSendFormWithNotCyrillicSymbolsByName() {
        $("[data-test-id=city] .input__control").setValue(validUser.getCity());
        $("[data-test-id=date] .input__control").
                setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        $("[data-test-id=name] .input__control").setValue(validUserEn.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=name].input_invalid .input__sub").shouldHave(text("Имя и Фамилия указаные неверно."));
    }

    @Test
    public void shouldSendFormWithCityNotInList() {
        $("[data-test-id=city] .input__control").setValue(DataGenerator.generateLocalCity());
        $("[data-test-id=date] .input__control")
                .setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        $("[data-test-id=name] .input__control").setValue(validUser.getName());
        $("[data-test-id=phone] .input__control").setValue(validUser.getPhone());
        $("[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();
        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(text("Доставка в выбранный город недоступна"));
    }

//    @Test
//    void shouldOrderCardWithPhone12Digits() {
//        $("[data-test-id=city] .input__control").setValue("Новосибирск");
//        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(5).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//        $("[data-test-id=name] .input__control").setValue("Андрей Грибанов");
//        $("[data-test-id=phone] .input__control").setValue("+712345678901");
//        $("[data-test-id=agreement]").click();
//        $(byText("Запланировать")).click();
//        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(text("Телефон указан неверно"));
//    }
//
//    @Test
//    void shouldOrderCardWithPhone10Digits() {
//        $("[data-test-id=city] .input__control").setValue("Новосибирск");
//        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//        $("[data-test-id=name] .input__control").setValue("Андрей Грибанов");
//        $("[data-test-id=phone] .input__control").setValue("+7123456789");
//        $("[data-test-id=agreement]").click();
//        $(byText("Запланировать")).click();
//        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(text("Телефон указан неверно"));
//    }
//
//    @Test
//    void shouldOrderCardWithPhoneWith11DigitsAndWithOutPlusSeven() {
//        $("[data-test-id=city] .input__control").setValue("Новосибирск");
//        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//        $("[data-test-id=name] .input__control").setValue(validUser.getName());
//        $("[data-test-id=phone] .input__control").setValue("81234567890");
//        $("[data-test-id=agreement]").click();
//        $(byText("Запланировать")).click();
//        $("[data-test-id=success-notification] .notification__title").shouldHave(text("Успешно!"), Duration.ofSeconds(15));
//    }
//
//    @Test
//    void shouldOrderCardWithPhoneValueHowString() {
//        $("[data-test-id=city] .input__control").setValue("Новосибирск");
//        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
//        $("[data-test-id=name] .input__control").setValue("Андрей Грибанов");
//        $("[data-test-id=phone] .input__control").setValue("test@test.ru");
//        $("[data-test-id=agreement]").click();
//        $(byText("Запланировать")).click();
//        $("[data-test-id=phone].input_invalid .input__sub").shouldHave(text("Телефон указан неверно"));
//    }
}
