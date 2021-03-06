package Controllers.Admin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Controllers.*;
import static Controllers.Controller.CURRENT_USER;
import Models.Logs;
import Models.UserDetails;
import Models.Users;
import java.util.Date;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.hibernate.Transaction;
import org.primefaces.event.FlowEvent;

/**
 *
 * @author Ayberk
 */
@ManagedBean
@ViewScoped
public class CreateUserBean extends Controller {

    private Users currentUser;
    Transaction tx;
// user

    private String tckn;
    private String name;
    private String surname;
    private String password;
    private String email;
    private boolean status;
    private String type;

    //userDetail  
    private String phone;
    private String adress;
    private Date birthday;
    private String gender = "MALE";
    private Double currentGpa;
    private String graduate;
    private String master;
    private String emergencyPhone;
    private String secretQuestion;
    private String secretAnswer;
    private Date registerDate;
    private Users userId;

    private boolean skip;

    /**
     * Creates a new instance of KullaniciEkleController
     */
    public CreateUserBean() {
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("Kullanici Ekle init()");
        setSession(HibernateUtil.getSessionFactory().openSession());
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        currentUser = (Users) sessionMap.get(CURRENT_USER);

        if (hasPermission(currentUser, Users.TYPE_ADMIN) == false) {
            return;
        }

    }

    @PreDestroy
    @Override
    public void destroy() {
        System.out.println("CourseManage destroy()");

        getSession().close();
    }

    public void insertNewUser() {
        System.out.println("insertNewUser()");
        FacesContext context = FacesContext.getCurrentInstance();

        int parse = 0;
        try {
            parse = Integer.parseInt(type);

        } catch (NumberFormatException e) {
        }

        if (!isValid(email)) {
            System.out.println("not valid e mail");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong Email Adress\nUser is not added.!.", ""));

            return;
        }
        tckn = tckn.replaceAll(" ", "");
        if (!isValidTCKN(tckn)) {
            System.out.println(">" + tckn + "<");
            System.out.println("not valid tckn");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "TCKN should be 11 digit.\nUser is not added.!.", ""));
            return;
        }

        Users user = new Users();

        user.setTckn(tckn);
        user.setName(name.replaceAll("i", "I").toUpperCase());
        user.setSurname(surname.replaceAll("i", "I").toUpperCase());
        user.setPassword(sha256(password));
        user.setEmail(email);
        user.setStatus(true);
        user.setType(parse);

        UserDetails userDetail = new UserDetails();

        if (gender.toLowerCase().charAt(0) == 'm') {
            gender = "MALE";
        } else {
            gender = "FEMALE";
        }

//        System.out.println(phone + "||" + adress + "||" + gender + master + "||" + emergencyPhone + "||" + secretAnswer);
        userDetail.setPhone(phone);
        userDetail.setAdress(adress.replaceAll("i", "I").toUpperCase());
        userDetail.setBirthday(birthday);
        userDetail.setGender(gender.toUpperCase());
        userDetail.setGraduate(graduate.replaceAll("i", "I").toUpperCase());
        userDetail.setMaster(master.replaceAll("i", "I").toUpperCase());
        userDetail.setEmergencyPhone(emergencyPhone);
        userDetail.setSecretQuestion(secretQuestion);
        userDetail.setSecretAnswer(sha256(secretAnswer));
        userDetail.setRegisterDate(new Date());
        userDetail.setUserId(user);

        try {
            tx = getSession().beginTransaction();
            getSession().save(user);
            getSession().save(userDetail);
            getSession().save(new Logs(Logs.USER_NEW, "new user created", currentUser, new Date()));
            System.out.println("Kullanici Eklendi !");
            resetValues();
            tx.commit();

        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "User creating FAILED!.", ""));
            e.printStackTrace();
            getSession().getTransaction().rollback();
            return;
        }
        context.addMessage(null, new FacesMessage(user.getUserId() + " Numarali Kullanici Eklendi."));

    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    public static boolean isValidTCKN(String tckn) {
        return tckn.length() == 11;
    }

    public String onFlowProcess(FlowEvent event) {

        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }

    public void cancelCreate() {
        System.out.println("Iptal");
        resetValues();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(" Canceled"));
    }

    public void resetValues() {
        name = null;
        surname = null;
        password = null;
        email = null;
        tckn = "";
        phone = null;
        adress = null;
        birthday = null;
        gender = null;
        currentGpa = null;
        graduate = null;
        master = null;
        emergencyPhone = null;
        secretQuestion = null;
        secretAnswer = null;
        registerDate = null;
        userId = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTckn() {
        return tckn;
    }

    public void setTckn(String tckn) {
        this.tckn = tckn;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getCurrentGpa() {
        return currentGpa;
    }

    public void setCurrentGpa(Double currentGpa) {
        this.currentGpa = currentGpa;
    }

    public String getGraduate() {
        return graduate;
    }

    public void setGraduate(String graduate) {
        this.graduate = graduate;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

}
