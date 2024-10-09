package uk.gov.companieshouse.efs.web;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EfsWebApplication {

    /**
     * Required spring boot application main method.
     *
     * @param args array of String arguments
     */
    public static void main(String[] args) {
        run(EfsWebApplication.class, args);
    }
}
