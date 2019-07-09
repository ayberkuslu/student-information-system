/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Giris;

import Controllers.Controller;
import Controllers.HibernateUtil;
import Models.*;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author hp_user
 */
@ApplicationScoped
@ManagedBean
public class LoginController extends Controller {

    private Integer userId;
    private String userPassword;
    private boolean authorized = false;

    @PreDestroy
    @Override
    public void destroy() {

        System.out.println("Controllers.Giris.LoginController.destroy()");
        getSession().close();
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("Controllers.Giris.LoginController.init()");
        setSession(HibernateUtil.getSessionFactory().openSession());
    }

    public void tryLogin() {

        try {
            this.authorized = isCorrectUser();
            goNextPage();
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private boolean isCorrectUser() {
        try {

//            setSession(HibernateUtil.getSessionFactory().openSession());
//            getSession().beginTransaction();
//            Users targetUser = (Users) getSession().get(Users.class, userId);
//            UserDetails targetUserDetail = (UserDetails) getSession().get(UserDetails.class, userId);
            Users targetUser = (Users) getObject(Users.class, userId);
//            UserDetails targetUserDetail = (UserDetails) getObject(UserDetails.class, userId);
//            getSession().close();

//            if (targetUser == null || targetUserDetail == null) {
//                return false;
//            }
            if (targetUser.getPassword().equals(sha256(userPassword))) {
                Map<String, Object> sessionMap = FacesContext.getCurrentInstance().
                        getExternalContext().getSessionMap();

                sessionMap.put("kullanici", targetUser);

                setCurrentUser(targetUser);
//                setCurrentUserDetail(targetUserDetail);
                insertObject(new Logs(Logs.USER_LOGIN, "succesfully logined", targetUser, new Date()));

                return true;
            }

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("<<HiberNateException \nAranan ID : " + userId + "\n Aranan sifre : " + userPassword
                    + "\n Aranan hash : " + sha256(userPassword));

        }
        return false;
    }

    private void goNextPage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        String truePage = "faces/homePage.xhtml";

        String falsePage = "index";
        if (authorized == true) {
            System.out.println("Giris Basarili. Kullanici : " + userId + "\n");
            context.getExternalContext().redirect(truePage);

            return;
        }
        context.addMessage(null, new FacesMessage("Authentication Failed. Check username or password."));

        System.out.println("Giris Basarisiz. Kullanici : " + userId + "\n");

    }

    public Integer getUserId() {

        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

}
