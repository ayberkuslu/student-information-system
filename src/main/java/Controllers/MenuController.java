/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Models.Logs;
import Models.Users;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import javax.faces.context.FacesContext;

/**
 *
 * @author Ayberk
 *
 */
@ManagedBean
@SessionScoped
public class MenuController extends Controller {

    private boolean renderedAdmin = false;
    private boolean renderedTeacher = false;
    private boolean renderedStudent = true;

    /**
     * Creates a new instance of MenuController
     */
    public MenuController() {
    }

    @PreDestroy
    @Override
    public void destroy() {

        System.out.println("Controllers.MenuController.destroy()");
        getSession().close();
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("Controllers.MenuController.init()");
        setSession(HibernateUtil.getSessionFactory().openSession());
    }

    public Users whoAmI() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap();
        Users temp = (Users) sessionMap.get(CURRENT_USER);
        switch (temp.getType()) {
            case Users.TYPE_ADMIN:
                setRenderedForAdmin();
                break;
            case Users.TYPE_TEACHER:
                setRenderedForTeacher();
                break;
            case Users.TYPE_STUDENT:
                setRenderedForStudent();
                break;
            default:
                break;
        }
        System.out.println("WhoAmI User toString(): " + temp);

        return temp;

    }

    public void logout() {
        System.out.println("logout girdi");
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().
                getExternalContext().getSessionMap();
        Users targetUser;
        targetUser = (Users) sessionMap.get(Controller.CURRENT_USER);
        sessionMap.remove(Controller.CURRENT_USER);
        sessionMap.clear();

        insertObject(new Logs(Logs.USER_LOGOUT, "logined out", targetUser, new Date()));
        System.out.println("logout basarili1");

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();

        try {
            context.getExternalContext().redirect(PAGE_LOGIN);
            System.out.println("Yonlendirme basarili");
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setRenderedForAdmin() {

        renderedAdmin = true;
        renderedTeacher = true;
        renderedStudent = true;

    }

    public void setRenderedForTeacher() {

        renderedAdmin = false;
        renderedTeacher = true;
        renderedStudent = true;

    }

    public void setRenderedForStudent() {
        renderedAdmin = false;
        renderedTeacher = false;
        renderedStudent = true;

    }

    public boolean isRenderedAdmin() {
        return renderedAdmin;
    }

    public void setRenderedAdmin(boolean renderedAdmin) {
        this.renderedAdmin = renderedAdmin;
    }

    public boolean isRenderedTeacher() {
        return renderedTeacher;
    }

    public void setRenderedTeacher(boolean renderedTeacher) {
        this.renderedTeacher = renderedTeacher;
    }

    public boolean isRenderedStudent() {
        return renderedStudent;
    }

    public void setRenderedStudent(boolean renderedStudent) {
        this.renderedStudent = renderedStudent;
    }



}
