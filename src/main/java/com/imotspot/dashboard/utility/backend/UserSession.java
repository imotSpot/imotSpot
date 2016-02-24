package com.imotspot.dashboard.utility.backend;

import com.imotspot.dashboard.utility.backend.service.UserFacade;
import org.apache.deltaspike.core.api.config.ConfigResolver;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;

@SessionScoped
public class UserSession implements Serializable {

    @Inject
    UserFacade userFacade;

//    @Inject
//    InvoicerFacade invoicerFacade;
//
//    @Inject
//    ProductFacade productFacade;

    private User user;

    @PostConstruct
    public void init() {
        final String propertyValue = ConfigResolver.getPropertyValue(
                "imotspot.gpluskey");
        // If no Google OAuth API key available, use fake login
//        if (StringUtils.isEmpty(propertyValue)) {
//            demoLogin();
//        }
    }

    protected void demoLogin() {
        final String email = "matti.meikalainen@gmail.com";
        this.user = userFacade.findByEmail(email);
        if (user == null) {
            this.user = userFacade.save(new User(email));

//            Invoicer invoicer = new Invoicer();
//            invoicer.setName("Matin pummpu ja imu");
//            invoicer.setAddress("Ruukinkatu 4, 20100 Turku");
//            invoicer.setBankAccount("FI1234567890");
//            invoicer.setEmail("matti@pumppu.fi");
//            invoicer.setPhone("+34567890");
//            invoicer.getAdministrators().add(this.user);
//            this.user.getAdministrates().add(invoicer);
//            invoicer = invoicerFacade.save(invoicer);
//            Product product = new Product();
//            product.setName("Pumppu");
//            product.setPrice(30.0);
//            product.setInvoicer(invoicer);
//            productFacade.save(product);
//            product = new Product();
//            product.setName("Imuri");
//            product.setPrice(60.0);
//            product.setInvoicer(invoicer);
//            productFacade.save(product);

        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void login(String email, String displayName) {
        try {
            user = userFacade.findByEmail(email);
        } catch (Exception e) {
        }
        if (user == null) {
            userFacade.save(new User(email));
            user = userFacade.findByEmail(email);
        }
    }

}
