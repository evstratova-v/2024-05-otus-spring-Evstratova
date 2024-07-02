package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw.domain.Student;
import ru.otus.hw.security.LoginStudentContext;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.TestService;

@ShellComponent(value = "Test Student Application Commands")
@RequiredArgsConstructor
public class ApplicationCommands {

    private final LoginStudentContext loginStudentContext;

    private final TestService testService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    @ShellMethod(value = "Login student", key = {"login", "l"})
    public String loginStudent(String firstName, String lastName) {
        Student student = new Student(firstName, lastName);
        loginStudentContext.login(student);
        return ioService.getMessage("ApplicationCommands.greeting", student.getFullName());
    }

    @ShellMethod(value = "Test student", key = {"test", "t"})
    @ShellMethodAvailability(value = "isTestCommandAvailable")
    public String testStudent() {
        var testResult = testService.executeTestFor(loginStudentContext.getStudent());
        resultService.showResult(testResult);
        return ioService.getMessage("ApplicationCommands.testEnd");
    }

    private Availability isTestCommandAvailable() {
        return loginStudentContext.isStudentLoggedIn()
                ? Availability.available()
                : Availability.unavailable(ioService.getMessage("ApplicationCommands.needLogin"));
    }
}
