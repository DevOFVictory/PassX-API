package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.returnables.DefaultReturnable;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class MyErrorController implements ErrorController {


    @RequestMapping("")
    public DefaultReturnable handleError() {

        return new DefaultReturnable(404, "API ressource not found. Please read the api-documentation.");

    }
}
